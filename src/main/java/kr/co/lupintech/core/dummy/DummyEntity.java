package kr.co.lupintech.core.dummy;

import kr.co.lupintech.model.alarm.Alarm;
import kr.co.lupintech.model.leave.Leave;
import kr.co.lupintech.model.leave.enums.LeaveStatus;
import kr.co.lupintech.model.leave.enums.LeaveType;
import kr.co.lupintech.model.user.User;
import kr.co.lupintech.model.user.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DummyEntity {
    public User newUser(String username, String email, Boolean status, LocalDate hireDate, Integer remainDays){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode("1234"))
                .email(email)
                .role(UserRole.ROLE_USER)
                .status(status)
                .hireDate(hireDate) // 입사 1년차라 가정
                .remainDays(remainDays)
                .build();
    }

    public Leave newLeave(User user, LeaveType type, LocalDate startDate, LocalDate endDate, Integer usingDays, LeaveStatus status){
        return Leave.builder()
                .user(user)
                .type(type)
                .startDate(startDate)
                .endDate(endDate)
                .usingDays(usingDays)
                .status(status)
                .build();
    }

    public User newMockUser(Long id, String username, String email, Integer remainDays){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .id(id)
                .username(username)
                .password(passwordEncoder.encode("1234"))
                .email(email)
                .role(UserRole.ROLE_USER)
                .status(true)
                .hireDate(LocalDate.now().minusYears(1).minusWeeks(1)) // 입사 1년차라 가정
                .remainDays(remainDays)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Leave newMockLeave(Long id, User user, LeaveType type, LocalDate startDate, LocalDate endDate, Integer usingDays){
        return Leave.builder()
                .id(id)
                .user(user)
                .type(type)
                .startDate(startDate)
                .endDate(endDate)
                .usingDays(usingDays)
                .status(LeaveStatus.WAITING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Alarm newMockAlarm(Long id, User user, Leave leave){
        Alarm alarm = Alarm.builder().id(id).user(user).leave(leave).createdAt(LocalDateTime.now()).build();
        leave.getAlarms().add(alarm);

        return alarm;
    }
}
