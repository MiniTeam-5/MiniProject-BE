package kr.co.lupintech.core.util;

import kr.co.lupintech.model.leave.Leave;
import kr.co.lupintech.model.leave.LeaveRepository;
import kr.co.lupintech.model.user.UserRepository;
import kr.co.lupintech.service.DateService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import kr.co.lupintech.model.leave.enums.LeaveStatus;
import kr.co.lupintech.model.user.User;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Component
public class MyScheduleUtil {

    private final LeaveRepository leaveRepository;

    private final UserRepository userRepository;

    private final DateService dateService;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정(0시)
    @Transactional
    public void everydayWaitingRemove() { // 대기 상태인 leave 지우기
        List<Leave> leavePSs = leaveRepository.findByStartDateAndStatus(LocalDate.now(), LeaveStatus.WAITING);

        for(int i = 0; i < leavePSs.size(); i++){
            Leave leavePS = leavePSs.get(i);
            User userPS = leavePSs.get(i).getUser();

            userPS.increaseRemainDays(leavePS.getUsingDays());
            leaveRepository.delete(leavePS);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정(0시)
    @Transactional
    public void everydayUpdateAnnualDays() { // 입사일로부터 n년 되는 사원의 연차 새로 주기.
        List<User> userPSs = userRepository.findByHireDate(LocalDate.now().plusYears(1)
                , LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());

        for(int i = 0; i < userPSs.size(); i++){
            User userPS = userPSs.get(i);

            int annualLimit = dateService.getAnnualLimit(userPS.getHireDate());
            userPS.setRemainDays(annualLimit);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정(0시)
    @Transactional
    public void everydayAddAnnualDay() { // 1년 안된 신입들은 입사 후 한 달마다 1일씩 올려주기
        List<User> userPSs = userRepository.findNewByHireDate(LocalDate.now().plusYears(1)
                , LocalDate.now().getDayOfMonth());

        for(int i = 0; i < userPSs.size(); i++){
            User userPS = userPSs.get(i);

            if(userPS.getHireDate().equals(LocalDate.now())) continue; // 오늘 입사한 사람은 연차 없음.
            userPS.increaseRemainDays(1);
        }
    }
}
