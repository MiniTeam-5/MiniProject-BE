package shop.mtcoding.restend.core.dummy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
}
