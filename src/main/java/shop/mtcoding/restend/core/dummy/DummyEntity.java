package shop.mtcoding.restend.core.dummy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.dto.manage.Manage;
import shop.mtcoding.restend.model.user.User;

import java.time.LocalDateTime;

public class DummyEntity {
    public User newUser(String username, String fullName){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode("1234"))
                .fullName(fullName)
                .email(username+"@nate.com")
                .role("USER")
                .remain_days(2)
                .hire_date(LocalDateTime.now())
                .status(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public User newMockUser(Long id, String username, String fullName,String role,Integer remain_days){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .id(id)
                .username(username)
                .password(passwordEncoder.encode("1234"))
                .fullName(fullName)
                .email(username+"@nate.com")
                .role(role)
                .remain_days(remain_days)
                .status(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Manage.UserManageDTO newMockChartUser(Long userId, String role, String username, LocalDateTime hire_date, Integer remain_days, String profile){
        return Manage.UserManageDTO.builder()
                .userId(userId)
                .role(role)
                .username(username)
                .hire_date(hire_date)
                .remain_days(remain_days)
                .profile(profile)
                .build();
    }

}
