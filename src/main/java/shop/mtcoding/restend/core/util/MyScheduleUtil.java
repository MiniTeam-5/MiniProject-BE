package shop.mtcoding.restend.core.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.mtcoding.restend.model.leave.Leave;
import shop.mtcoding.restend.model.leave.LeaveRepository;
import shop.mtcoding.restend.model.leave.enums.LeaveStatus;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Component
public class MyScheduleUtil {

    private final LeaveRepository leaveRepository;

    public MyScheduleUtil(LeaveRepository leaveRepository) {
        this.leaveRepository = leaveRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정(0시)
    public void checkLeaves() {
        LocalDate today = LocalDate.now();
        System.out.println(today);

        List<Leave> leavePSs = leaveRepository.findByStartDateAndStatus(today, LeaveStatus.WAITING);
        for(int i = 0; i < leavePSs.size(); i++){
            Leave leavePS = leavePSs.get(i);
            User UserPS = leavePSs.get(i).getUser();
            UserPS.increaseRemainDays(leavePS.getUsingDays());
            leaveRepository.delete(leavePS);
        }
    }
}
