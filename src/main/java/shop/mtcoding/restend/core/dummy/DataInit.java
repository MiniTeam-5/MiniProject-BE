package shop.mtcoding.restend.core.dummy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;
import shop.mtcoding.restend.model.user.UserRole;

import java.time.LocalDate;

@Component
public class DataInit extends DummyEntity{

    @Profile("dev")
    @Bean
    CommandLineRunner init(UserRepository userRepository){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return args -> {
            userRepository.save(newUser("ssar", true, LocalDate.now().minusYears(1).minusWeeks(1), 15));
            userRepository.save(newUser("cos", true, LocalDate.now().minusYears(1).minusWeeks(1), 15));
            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("1234"))
                    .email("admin"+"@nate.com")
                    .role(UserRole.ROLE_ADMIN)
                    .status(true)
                    .hireDate(LocalDate.now().minusYears(1).minusWeeks(1)) // 입사 1년차라 가정
                    .remainDays(15)
                    .build());
            userRepository.save(User.builder()
                    .username("master")
                    .password(passwordEncoder.encode("1234"))
                    .email("master"+"@nate.com")
                    .role(UserRole.ROLE_MASTER)
                    .status(true)
                    .hireDate(LocalDate.now().minusYears(1).minusWeeks(1)) // 입사 1년차라 가정
                    .remainDays(15)
                    .build());
            userRepository.save(User.builder()
                    .username("oneyear")
                    .password(passwordEncoder.encode("1234"))
                    .email("oneyear"+"@nate.com")
                    .role(UserRole.ROLE_USER)
                    .status(true)
                    .hireDate(LocalDate.now().minusYears(1)) // 입사 1년차라 가정
                    .remainDays(0)
                    .build());
            userRepository.save(User.builder()
                    .username("newcomer")
                    .password(passwordEncoder.encode("1234"))
                    .email("newcomer"+"@nate.com")
                    .role(UserRole.ROLE_USER)
                    .status(true)
                    .hireDate(LocalDate.now().minusMonths(2)) // 입사 2개월이라 가정
                    .remainDays(1)
                    .build());
        };
    }
}
