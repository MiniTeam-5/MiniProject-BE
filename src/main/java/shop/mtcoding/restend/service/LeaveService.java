package shop.mtcoding.restend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.restend.core.exception.Exception400;
import shop.mtcoding.restend.core.exception.Exception500;
import shop.mtcoding.restend.dto.alarm.AlarmResponse;
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
import shop.mtcoding.restend.model.user.UserRole;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LeaveService {
    private final UserRepository userRepository;
    private final LeaveRepository leaveRepository;
    private final AlarmRepository alarmRepository;

    private final DateService dateService;
    private final SseService sseService;

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
        // 1) 사용할 연차 일수 계산하기: 평일만 계산 + 공휴일 계산 by 공공 API
        Integer usingDays = -1;
        try{
            usingDays = dateService.getWeekDayCount(applyInDTO.getStartDate(), applyInDTO.getEndDate());
        }catch (URISyntaxException e){
            throw new Exception500(e.getMessage());
        }

        if(usingDays == 0){
            throw new Exception400("startDate, endDate", "연차를 0일 신청했습니다.");
        }
        if(usingDays > userPS.getRemainDays()){
            throw new Exception400("startDate, endDate", "남은 연차보다 더 많이 신청했습니다.");
        }

        // 이미 신청한 날이 껴있는 경우
        if(leaveRepository.existsDuplicateAnnual(applyInDTO.getType(), applyInDTO.getStartDate(), applyInDTO.getEndDate(),userId)){
            throw new Exception400("startDate, endDate", "이미 신청한 연차일이 포함된 신청입니다.");
        }

        // 3) 사용자의 남은 연차 일수 업데이트
        userPS.useAnnualLeave(usingDays);

        // 4) 알림 등록
        String content = userPS.getUsername()+"님의 "+applyInDTO.getStartDate().toString()+"부터 "
                +applyInDTO.getEndDate()+"까지, 총 "+usingDays+"일의 연차 신청이 완료되었습니다.";
        Alarm alarmPS = alarmRepository.save(Alarm.builder().user(userPS).content(content).build());

        // 5) 모든 관리자에게 실시간 알람 전송
        Set<UserRole> adminAndMasterRoles = new HashSet<>(Arrays.asList(UserRole.ROLE_ADMIN, UserRole.ROLE_MASTER));
        List<User> managerList = userRepository.findByRoles(adminAndMasterRoles);
        for (User manager : managerList) {
            sseService.sendToUser(manager.getId(), "alarm", new AlarmResponse.AlarmOutDTO(alarmPS));
        }

        // 6) 연차 등록
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

        Alarm alarmPS = alarmRepository.save(Alarm.builder().user(userPS).content(content).build());
        sseService.sendToUser(userPS.getId(), "alarm", new AlarmResponse.AlarmOutDTO(alarmPS));

        return new LeaveResponse.DecideOutDTO(userPS);
    }


    @Transactional(readOnly = true)
    public List<LeaveResponse.InfoOutDTO> 상태선택연차당직정보가져오기(LeaveStatus status)
    {
        // 대기 상태의 모든 연차/당직 정보 가져오기
        List<Leave> leaves = leaveRepository.findByStatus(status);

        // 반환할 DTO 리스트 생성
        List<LeaveResponse.InfoOutDTO> infoOutDTOList = leaves.stream()
                .map(leave -> new LeaveResponse.InfoOutDTO(leave, leave.getUser()))
                .collect(Collectors.toList());

        return infoOutDTOList;
    }

    @Transactional(readOnly = true)
    //모든 유저의 특정 월 정보
    public List<LeaveResponse.InfoOutDTO> 연차당직정보가져오기세달치(String month) {

        // '연도-월' 형식 검증
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        try {
            TemporalAccessor parsedMonth = formatter.parse(month);
        } catch (DateTimeParseException e) {
            throw new Exception400("InvalidFormat", "날짜 형식은 yyyy-MM 이어야 함");
        }

        month += "-01"; // '연도-월' 에 '-일' 붙이기

        LocalDate currentDate = LocalDate.parse(month);
        LocalDate startDate = currentDate.minusMonths(1).withDayOfMonth(1); // 이전 달의 첫날
        LocalDate endDate = currentDate.plusMonths(1).withDayOfMonth(currentDate.getMonth().plus(1).length(currentDate.isLeapYear())); // 다음 달의 마지막 날

        // startDate와 endDate 사이에 있는 모든 연차/당직 정보 가져오기
        List<Leave> leaves = leaveRepository.findAll()
                .stream()
                .filter(leave -> (leave.getStartDate().compareTo(startDate) >= 0 && leave.getStartDate().compareTo(endDate) <= 0)
                        || (leave.getEndDate().compareTo(startDate) >= 0 && leave.getEndDate().compareTo(endDate) <= 0)
                        || (leave.getStartDate().compareTo(startDate) <= 0 && leave.getEndDate().compareTo(endDate) >= 0))
                .collect(Collectors.toList());

        // 반환할 DTO 리스트 생성
        List<LeaveResponse.InfoOutDTO> infoOutDTOList = leaves.stream()
                .map(leave -> new LeaveResponse.InfoOutDTO(leave, leave.getUser()))
                .collect(Collectors.toList());

        return infoOutDTOList;
    }

    @Transactional(readOnly = true)
    public List<LeaveResponse.InfoOutDTO> 특정유저연차당직정보가져오기(Long userId) {

        // 특정 유저의 모든 연차/당직 정보 가져오기
        List<Leave> leaves = leaveRepository.findAllByUserId(userId);

        // 반환할 DTO 리스트 생성
        List<LeaveResponse.InfoOutDTO> infoOutDTOList = leaves.stream()
                .map(leave -> new LeaveResponse.InfoOutDTO(leave, leave.getUser()))
                .collect(Collectors.toList());

        return infoOutDTOList;
    }

}
