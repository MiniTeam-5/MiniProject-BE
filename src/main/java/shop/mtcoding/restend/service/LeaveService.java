package shop.mtcoding.restend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.restend.core.exception.Exception400;
import shop.mtcoding.restend.core.exception.Exception500;
import shop.mtcoding.restend.core.util.MyDateUtil;
import shop.mtcoding.restend.dto.leave.LeaveRequest;
import shop.mtcoding.restend.dto.leave.LeaveResponse;
import shop.mtcoding.restend.model.alarm.Alarm;
import shop.mtcoding.restend.model.alarm.AlarmRepository;
import shop.mtcoding.restend.model.leave.Leave;
import shop.mtcoding.restend.model.leave.LeaveRepository;
import shop.mtcoding.restend.model.leave.enums.LeaveStatus;
import shop.mtcoding.restend.model.leave.enums.LeaveType;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LeaveService {
    private final UserRepository userRepository;
    private final LeaveRepository leaveRepository;
    private final AlarmRepository alarmRepository;


    @Transactional
    public LeaveResponse.ApplyOutDTO 연차당직신청하기(LeaveRequest.ApplyInDTO applyInDTO, Long userId) {
        // 1. 유저 존재 확인
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception500("로그인 된 유저가 DB에 존재하지 않음")
        );
        // 2. 당직인 경우
        if(applyInDTO.getType().equals(LeaveType.DUTY)){
            if(!applyInDTO.getStartDate().equals(applyInDTO.getEndDate())){
                throw new Exception400("startDate, endDate", "startDate와 endDate가 같아야 합니다.");
            }

            // 이미 신청한 날인 경우
            if(leaveRepository.existsDuplicateDuty(applyInDTO.getType(), applyInDTO.getStartDate(), userId)){
                throw new Exception400("startDate, endDate", "중복된 당직 신청입니다.");
            }

            // 1) 알림 등록
            String content = userPS.getUsername()+"님의 "+applyInDTO.getStartDate()+"일 당직 신청이 완료되었습니다.";
            alarmRepository.save(Alarm.builder().user(userPS).content(content).build());

            // 2) 당직 등록
            Leave leavePS = leaveRepository.save(applyInDTO.toEntity(userPS, 0));
            return new LeaveResponse.ApplyOutDTO(leavePS, userPS);
        }

        // 3. 연차인 경우
        // 1) 사용할 연차 일수 계산하기
        // 버전1 : 평일만 계산
        Integer usingDays = MyDateUtil.getWeekDayCount(applyInDTO.getStartDate(), applyInDTO.getEndDate());
        // 버전2 : 공휴일 계산 코드 사용
        // 버전3 : 공공 API 이용

        if(usingDays == 0){
            throw new Exception400("startDate, endDate", "연차를 0일 신청했습니다.");
        }
        if(usingDays > userPS.getRemainDays()){
            throw new Exception400("startDate, endDate", "남은 연차보다 더 많이 신청했습니다.");
        }

        // 이미 신청한 날이 껴있는 경우
        if(leaveRepository.findMyAnnualAfter(applyInDTO.getType(), applyInDTO.getStartDate(), applyInDTO.getEndDate(),userId)){
            throw new Exception400("startDate, endDate", "이미 신청한 연차일이 포함된 신청입니다.");
        }

        // 3) 사용자의 남은 연차 일수 업데이트
        userPS.useAnnualLeave(usingDays);

        // 4) 알림 등록
        String content = userPS.getUsername()+"님의 "+applyInDTO.getStartDate().toString()+"부터 "
                +applyInDTO.getEndDate()+"까지, 총 "+usingDays+"일의 연차 신청이 완료되었습니다.";
        alarmRepository.save(Alarm.builder().user(userPS).content(content).build());

        // 5) 연차 등록
        Leave leavePS = leaveRepository.save(applyInDTO.toEntity(userPS, usingDays));
        return new LeaveResponse.ApplyOutDTO(leavePS, userPS);
    }

    @Transactional
    public LeaveResponse.CancelOutDTO 연차당직신청취소하기(Long id, Long userId) {
        Leave leavePS = leaveRepository.findById(id).orElseThrow(
                () -> new Exception500("해당 연차/당직 신청 정보가 DB에 존재하지 않음")
        );
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception500("로그인 된 유저가 DB에 존재하지 않음")
        );

        if(leavePS.getStatus().equals(LeaveStatus.APPROVAL)){
            throw new Exception400("id", "이미 승인된 신청입니다.");
        }
        if(leavePS.getStatus().equals(LeaveStatus.REJECTION)){
            throw new Exception400("id", "이미 거절된 신청입니다.");
        }

        String content = "";
        if(leavePS.getType().equals(LeaveType.ANNUAL)){
            userPS.increaseRemainDays(leavePS.getUsingDays());
            content = userPS.getUsername()+"님의 "+leavePS.getStartDate().toString()+"부터 "
                    +leavePS.getEndDate()+"까지, 총 "+leavePS.getUsingDays()+"일의 연차 신청이 취소되었습니다.";
        } else {
            content = userPS.getUsername()+"님의 "+leavePS.getStartDate()+"일 당직 신청이 취소되었습니다.";
        }

        leaveRepository.delete(leavePS);
        alarmRepository.save(Alarm.builder().user(userPS).content(content).build());
        return new LeaveResponse.CancelOutDTO(userPS);
    }

    @Transactional
    public LeaveResponse.DecideOutDTO 연차당직결정하기(LeaveRequest.DecideInDTO decideInDTO) {
        Leave leavePS = leaveRepository.findById(decideInDTO.getId()).orElseThrow(
                () -> new Exception500("해당 연차/당직 신청 정보가 DB에 존재하지 않음")
        );
        User userPS = leavePS.getUser();
        if (!userPS.getStatus()) {
            throw new Exception500("탈퇴한 회원의 신청입니다.");
        }
        if (leavePS.getStatus().equals(LeaveStatus.APPROVAL)) {
            throw new Exception400("id", "이미 승인된 신청입니다.");
        }
        if (leavePS.getStatus().equals(LeaveStatus.REJECTION)) {
            throw new Exception400("id", "이미 거절된 신청입니다.");
        }

        boolean isReject = false;
        if (decideInDTO.getStatus().equals(LeaveStatus.APPROVAL)) {
            leavePS.setStatus(LeaveStatus.APPROVAL);
        } else {
            leavePS.setStatus(LeaveStatus.REJECTION);
            isReject = true;
        }

        String content = "";
        String status = isReject ? "거절" : "승인";
        if (leavePS.getType().equals(LeaveType.ANNUAL)) {
            if (isReject) userPS.increaseRemainDays(leavePS.getUsingDays());
            content = userPS.getUsername() + "님의 " + leavePS.getStartDate().toString() + "부터 "
                    + leavePS.getEndDate() + "까지, 총 " + leavePS.getUsingDays() + "일의 연차 신청이 " +
                    status + "되었습니다.";
        } else {
            content = userPS.getUsername() + "님의 " + leavePS.getStartDate() + "일 당직 신청이 " + status + "되었습니다.";
        }

        alarmRepository.save(Alarm.builder().user(userPS).content(content).build());
        return new LeaveResponse.DecideOutDTO(userPS);
    }

    //처리해야할 경우의 수
    //모든 유저의 서버 기준 월 정보
    //모든 유저의 특정 월 정보
    //모든 유저의 특정 주 정보
    //모든 유저의 특정 일 정보

    //특정 유저의 서버 기준 월 정보
    //특정 유저의 특정 월 정보
    //특정 유저의 특정 주 정보
    //특정 유저의 특정 일 정보

    //month, week, day 은 모두 특정한 날짜이다. 같은 형식이다. 다만 월에 속한 연차정보, 한주에 속한 연차 정보, 하루에 속한 연차 정보를
    //구분하기 위해 만들었음
    public List<LeaveResponse.InfoOutDTO> getLeaves(Long id, String month, String week, String day) {

        int nonNullCount = 0;
        if (month != null) nonNullCount++;
        if (week != null) nonNullCount++;
        if (day != null) nonNullCount++;

        if (nonNullCount > 1) {
            throw new Exception400("month, week, day", "month, week, day 중 하나만 값이 있어야 합니다.");
        }

        List<Leave> leaves;
        // 특정 유저의 전체 일정 정보
        if (id != null) {
            leaves = leaveRepository.findAllByUserId(id);
        }
        // 모든 유저의 일정 정보
        else {
            leaves = leaveRepository.findAll();
        }

        LocalDate today = LocalDate.now();

        if (nonNullCount == 0)//지정한 month, week, day 없는 경우 서버시간 기준으로 월 정보를 보낸다
        {
            LocalDate copyToday = today;
            leaves = leaves.stream()
                    .filter(leave -> (leave.getStartDate().getMonth() == copyToday.getMonth() && leave.getStartDate().getYear() == copyToday.getYear())
                            || (leave.getEndDate().getMonth() == copyToday.getMonth() && leave.getEndDate().getYear() == copyToday.getYear()))
                    .collect(Collectors.toList());
        } else if (nonNullCount == 1) {
            if (month != null)//지정한 날짜를 포함하는 월을 구하고 그 월에 걸친 모든 연차를 찾아서 반환
            {
                LocalDate copyToday = LocalDate.parse(month);
                leaves = leaves.stream()
                        .filter(leave -> (leave.getStartDate().getMonth() == copyToday.getMonth() && leave.getStartDate().getYear() == copyToday.getYear())
                                || (leave.getEndDate().getMonth() == copyToday.getMonth() && leave.getEndDate().getYear() == copyToday.getYear()))
                        .collect(Collectors.toList());
            } else if (week != null)//지정한 날짜를 포함하는 주를 구하고 그 주에 걸친 모든 연차를 찾아서 반환
            {
                LocalDate copyToday = LocalDate.parse(week);
                LocalDate weekStartDate = copyToday.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
                LocalDate weekEndDate = copyToday.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

                leaves = leaves.stream()
                        .filter(leave -> ((leave.getStartDate().isBefore(weekEndDate)) || (leave.getStartDate().isEqual(weekEndDate)))
                                && ((leave.getEndDate().isAfter(weekStartDate)) || (leave.getEndDate().isEqual(weekStartDate))))
                        .collect(Collectors.toList());

            } else if (day != null)//지정한 날짜를 포함하는 모든 연차를 찾아서 반환
            {
                LocalDate copyToday = LocalDate.parse(day);
                leaves = leaves.stream()
                        .filter(leave -> (leave.getStartDate().isEqual(copyToday) || (leave.getStartDate().isBefore(copyToday)))
                                && ((leave.getEndDate().isEqual(copyToday)) || (leave.getEndDate().isAfter(copyToday))))
                        .collect(Collectors.toList());
            }

        }

        // 반환할 DTO 리스트 생성
        List<LeaveResponse.InfoOutDTO> infoOutDTOList = leaves.stream()
                .map(leave -> new LeaveResponse.InfoOutDTO(leave, leave.getUser()))
                .collect(Collectors.toList());

        return infoOutDTOList;
    }
}
