package shop.mtcoding.restend.core.dummy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DummyEntity {
    public User newUser(String username){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode("1234"))
                .email(username+"@nate.com")
                .role(UserRole.USER)
                .status(true)
                .hireDate(LocalDate.now())
                .build();
    }

    public User newMockUser(Long id, String username){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .id(id)
                .username(username)
                .password(passwordEncoder.encode("1234"))
                .email(username+"@nate.com")
                .role(UserRole.USER)
                .status(true)
                .hireDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
