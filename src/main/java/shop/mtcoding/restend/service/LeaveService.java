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

            // 추가 구현 : 이미 신청한 날 인 경우
            // findByStartDate in LeaveTABLE(where userId== id)

            // 1) 알림 등록
            String content = userPS.getUsername() + "님의 " + applyInDTO.getStartDate() + "일 당직 신청이 완료되었습니다.";
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
        // 추가 구현 : 이미 신청한 날인 경우
        // select leave where userid = id
        // 포문 돌면서 starDate와 endDated와 newStartDate와 newEndDate가 겹치는지 확인

        // 3) 사용자의 남은 연차 일수 업데이트
        userPS.useAnnualLeave(usingDays);

        // 4) 알림 등록
        String content = userPS.getUsername() + "님의 " + applyInDTO.getStartDate().toString() + "부터 "
                + applyInDTO.getEndDate() + "까지, 총 " + usingDays + "일의 연차 신청이 완료되었습니다.";
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
            content = userPS.getUsername() + "님의 " + leavePS.getStartDate().toString() + "부터 "
                    + leavePS.getEndDate() + "까지, 총 " + leavePS.getUsingDays() + "일의 연차 신청이 취소되었습니다.";
        } else {
            content = userPS.getUsername() + "님의 " + leavePS.getStartDate() + "일 당직 신청이 취소되었습니다.";
        }

        leaveRepository.delete(leavePS);
        alarmRepository.save(Alarm.builder().user(userPS).content(content).build());
        return new LeaveResponse.CancelOutDTO(userPS);
    }
}
