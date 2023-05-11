package kr.co.lupintech.core.dummy;

import kr.co.lupintech.dto.manage.ManageUserDTO;
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
    public User newUser(String username, Boolean status, LocalDate hireDate, Integer remainDays){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode("1234"))
                .email(username+"@nate.com")
                .role(UserRole.ROLE_USER)
                .status(status)
                .hireDate(hireDate) // 입사 1년차라 가정
                .remainDays(remainDays)
                .build();
    }

    public Alarm newAlarm(User user, String content){
        return Alarm.builder()
                .user(user)
                .content(content)
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

    public User newMockUser(Long id, String username, Integer remainDays){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .id(id)
                .username(username)
                .password(passwordEncoder.encode("1234"))
                .email(username+"@nate.com")
                .role(UserRole.ROLE_USER)
                .status(true)
                .hireDate(LocalDate.now().minusYears(1).minusWeeks(1)) // 입사 1년차라 가정
                .remainDays(remainDays)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public User newMockStateUser(Long id, String username, Integer remainDays,boolean status){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .id(id)
                .username(username)
                .password(passwordEncoder.encode("1234"))
                .email(username+"@nate.com")
                .role(UserRole.ROLE_USER)
                .status(status)
                .hireDate(LocalDate.now().minusYears(1).minusWeeks(1)) // 입사 1년차라 가정
                .remainDays(remainDays)
                .createdAt(LocalDateTime.now())
                .build();
    }


    public User newMockUserRole(Long id, String username, Integer remainDays,UserRole userRole){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .id(id)
                .username(username)
                .password(passwordEncoder.encode("1234"))
                .email(username+"@nate.com")
                .role(userRole)
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

    public Alarm newMockAlarm(Long id, User user, String content){
        return Alarm.builder()
                .id(id)
                .user(user)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public ManageUserDTO.ManageUserListDTO newMockChartUser(Long userId, UserRole role, String username, LocalDate hireDate, Integer remainDays, String profile){
        return ManageUserDTO.ManageUserListDTO.builder()
                .userId(userId)
                .role(role)
                .username(username)
                .hireDate(hireDate)
                .remainDays(remainDays)
                .profile(profile)
                .build();
    }

}
