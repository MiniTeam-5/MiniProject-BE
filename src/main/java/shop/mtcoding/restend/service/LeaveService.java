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
import shop.mtcoding.restend.model.leave.enumUtil.LeaveType;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;

@RequiredArgsConstructor
@Service
public class LeaveService {
    private final UserRepository userRepository;
    private final LeaveRepository leaveRepository;
    private final AlarmRepository alarmRepository;

    @Transactional
    public LeaveResponse.ApplyOutDTO 연차신청하기(LeaveRequest.ApplyInDTO applyInDTO, Long userId) {
        // 1. 유저 존재 확인
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception500("로그인 된 유저가 DB에 존재하지 않음")
        );
        // 2. 당직인 경우
        if(applyInDTO.getType().equals(LeaveType.DUTY)){
            if(!applyInDTO.getStartDate().equals(applyInDTO.getEndDate())){
                throw new Exception400("startDate, endDate", "당직일은 하루만 선택야 합니다.");
            }
            // 1) 알림 등록
            String content = userPS.getUsername() + "님의 당직 신청이 완료되었습니다.";
            alarmRepository.save(Alarm.builder().user(userPS).content(content).build());

            // 2) 당직 등록
            Leave leavePS = leaveRepository.save(applyInDTO.toEntity(userPS));
            return new LeaveResponse.ApplyOutDTO(leavePS);
        }

        // 3. 연차인 경우
        // 1) 사용자의 남은 연차일수 가져오기
        int remainDays = userPS.getAnnualCount();

        // 2) 사용할 연차 일수 계산하기
        // 버전1 : 평일만 계산
        Integer usingDays = MyDateUtil.getWeekDayCount(applyInDTO.getStartDate(), applyInDTO.getEndDate());
        // 버전2 : 공휴일 계산 코드 사용
        // 버전3 : 공공 API 이용

        if(usingDays == 0){
            throw new Exception400("startDate, endDate", "연차를 0일 신청했습니다.");
        }
        if(usingDays > remainDays){
            throw new Exception400("startDate, endDate", "남은 연차보다 더 많이 신청했습니다.");
        }

        // 3) 사용자의 남은 연차 일수 업데이트
        userPS.useAnnualLeave(usingDays);

        // 4) 알림 등록
        String content = userPS.getUsername() + "님의 " + applyInDTO.getStartDate().toString() + "부터 "
                + applyInDTO.getEndDate() + "까지, 총 " + usingDays + "일의 연차 신청이 완료되었습니다.";
        alarmRepository.save(Alarm.builder().user(userPS).content(content).build());

        // 5) 연차 등록
        Leave leavePS = leaveRepository.save(applyInDTO.toEntity(userPS));
        return new LeaveResponse.ApplyOutDTO(leavePS);
    }
}
