package shop.mtcoding.restend.core.dummy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.restend.dto.leave.LeaveRequest;
import shop.mtcoding.restend.model.alarm.Alarm;
import shop.mtcoding.restend.model.leave.Leave;
import shop.mtcoding.restend.model.leave.enumUtil.LeaveStatus;
import shop.mtcoding.restend.model.leave.enumUtil.LeaveType;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DummyEntity {
    public User newUser(String username, String fullName){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode("1234"))
                .fullName(fullName)
                .email(username+"@nate.com")
                .role(UserRole.USER)
                .status(true)
                .hireDate(LocalDate.now())
                .annualCount(15)
                .build();
    }

    public Alarm newAlarm(User user, String content){
        return Alarm.builder()
                .user(user)
                .content(content)
                .build();
    }

    public Leave newLeave(User user, LeaveType type, LocalDate date){
        return Leave.builder()
                .user(user)
                .type(type)
                .startDate(date)
                .endDate(date)
                .status(LeaveStatus.WAITING)
                .build();
    }

    public User newMockUser(Long id, String username, String fullName){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .id(id)
                .username(username)
                .password(passwordEncoder.encode("1234"))
                .fullName(fullName)
                .email(username+"@nate.com")
                .role(UserRole.USER)
                .status(true)
                .hireDate(LocalDate.now())
                .annualCount(15)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Leave newMockLeave(Long id, User user, LeaveType type, LocalDate startDate, LocalDate endDate){
        return Leave.builder()
                .id(id)
                .user(user)
                .type(type)
                .startDate(startDate)
                .endDate(endDate)
                .status(LeaveStatus.WAITING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Alarm newMockAlarm(Long id, User user, String content){
        return Alarm.builder()
                .id(id)
                .user(user)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
