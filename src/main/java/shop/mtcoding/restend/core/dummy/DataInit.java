package shop.mtcoding.restend.core.dummy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import shop.mtcoding.restend.model.leave.LeaveRepository;
import shop.mtcoding.restend.model.leave.enums.LeaveStatus;
import shop.mtcoding.restend.model.leave.enums.LeaveType;
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
            userRepository.save(User.builder()
                    .username("jin")
                    .password(passwordEncoder.encode("1234"))
                    .email("jin"+"@nate.com")
                    .role(UserRole.ROLE_MASTER)
                    .status(true)
                    .hireDate(LocalDate.now().minusYears(10).minusWeeks(1)) // 입사 10년차라 가정
                    .remainDays(15)
                    .build());
        };
    }

    @Profile("dev")
    @Bean
    CommandLineRunner initLeave(LeaveRepository leaveRepository, UserRepository userRepository) {
        return args -> {

            User jin = userRepository.findByUsername("jin").orElse(null);

            if (jin != null) {
                leaveRepository.save(newLeave(jin, LeaveType.ANNUAL, LocalDate.parse("2023-03-28"),
                        LocalDate.parse("2023-04-02"), 1, LeaveStatus.WAITING));
            }

            if (jin != null) {
                leaveRepository.save(newLeave(jin, LeaveType.ANNUAL, LocalDate.parse("2023-04-28"),
                        LocalDate.parse("2023-05-02"), 1, LeaveStatus.WAITING));
            }

            if (jin != null) {
                leaveRepository.save(newLeave(jin, LeaveType.ANNUAL, LocalDate.parse("2023-05-28"),
                        LocalDate.parse("2023-06-02"), 1, LeaveStatus.WAITING));
            }

            if (jin != null) {
                leaveRepository.save(newLeave(jin, LeaveType.ANNUAL, LocalDate.parse("2023-06-28"),
                        LocalDate.parse("2023-07-02"), 1, LeaveStatus.WAITING));
            }

            if (jin != null) {
                leaveRepository.save(newLeave(jin, LeaveType.ANNUAL, LocalDate.parse("2023-07-28"),
                        LocalDate.parse("2023-08-02"), 1, LeaveStatus.WAITING));
            }

            if (jin != null) {
                leaveRepository.save(newLeave(jin, LeaveType.ANNUAL, LocalDate.parse("2023-08-28"),
                        LocalDate.parse("2023-09-02"), 1, LeaveStatus.WAITING));
            }

            if (jin != null) {
                leaveRepository.save(newLeave(jin, LeaveType.ANNUAL, LocalDate.parse("2023-09-28"),
                        LocalDate.parse("2023-10-02"), 1, LeaveStatus.WAITING));
            }

            if (jin != null) {
                leaveRepository.save(newLeave(jin, LeaveType.ANNUAL, LocalDate.parse("2023-10-28"),
                        LocalDate.parse("2023-11-02"), 1, LeaveStatus.WAITING));
            }

            if (jin != null) {
                leaveRepository.save(newLeave(jin, LeaveType.ANNUAL, LocalDate.parse("2023-12-28"),
                        LocalDate.parse("2024-01-02"), 1, LeaveStatus.WAITING));
            }

        };
    }
}
