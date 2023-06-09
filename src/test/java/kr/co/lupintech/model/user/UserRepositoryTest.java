package kr.co.lupintech.model.user;

import kr.co.lupintech.core.exception.Exception400;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import kr.co.lupintech.core.dummy.DummyEntity;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Import(BCryptPasswordEncoder.class)
@ActiveProfiles("test")
@DataJpaTest
public class UserRepositoryTest extends DummyEntity {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        userRepository.save(newUser("김쌀쌀", "ssar@nate.com", true, LocalDate.now().minusYears(1).minusWeeks(1), 15));
        userRepository.save(newUser("박코스", "cos@nate.com", true, LocalDate.now().minusYears(1).minusWeeks(1), 15));
        userRepository.save(newUser("일년차", "oneyear@nate.com", true, LocalDate.now().minusYears(1), 0));
        userRepository.save(newUser("김신입", "newcomer@nate.com", true, LocalDate.now().minusMonths(2), 1));

        em.clear();
    }

    @Test
    public void findById() {
        // given
        Long id = 1L;

        // when
        Optional<User> userOP = userRepository.findById(id);
        if (userOP.isEmpty()) {
            throw new Exception400("id", "아이디를 찾을 수 없습니다");
        }
        User userPS = userOP.get();

        // then
        Assertions.assertThat(userPS.getId()).isEqualTo(1L);
        Assertions.assertThat(userPS.getUsername()).isEqualTo("김쌀쌀");
        Assertions.assertThat(
                passwordEncoder.matches("1234", userPS.getPassword())
        ).isEqualTo(true);
        Assertions.assertThat(userPS.getEmail()).isEqualTo("ssar@nate.com");
        Assertions.assertThat(userPS.getRole()).isEqualTo(UserRole.ROLE_USER);
        Assertions.assertThat(userPS.getStatus()).isEqualTo(true);
        Assertions.assertThat(userPS.getHireDate()).isEqualTo(LocalDate.now().minusYears(1).minusWeeks(1));
        Assertions.assertThat(userPS.getRemainDays()).isEqualTo(15);
        Assertions.assertThat(userPS.getCreatedAt().toLocalDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(userPS.getUpdatedAt()).isNull();
    }

    @Test
    public void findByUsername() {
        // given
        String username = "김쌀쌀";

        // when
        Optional<User> userOP = userRepository.findByUsername(username);
        if (userOP.isEmpty()) {
            throw new Exception400("username", "유저네임을 찾을 수 없습니다");
        }
        User userPS = userOP.get();

        // then
        Assertions.assertThat(userPS.getId()).isEqualTo(1L);
        Assertions.assertThat(userPS.getUsername()).isEqualTo("김쌀쌀");
        Assertions.assertThat(
                passwordEncoder.matches("1234", userPS.getPassword())
        ).isEqualTo(true);
        Assertions.assertThat(userPS.getEmail()).isEqualTo("ssar@nate.com");
        Assertions.assertThat(userPS.getRole()).isEqualTo(UserRole.ROLE_USER);
        Assertions.assertThat(userPS.getStatus()).isEqualTo(true);
        Assertions.assertThat(userPS.getHireDate()).isEqualTo(LocalDate.now().minusYears(1).minusWeeks(1));

        Assertions.assertThat(userPS.getRemainDays()).isEqualTo(15);
        Assertions.assertThat(userPS.getCreatedAt().toLocalDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(userPS.getUpdatedAt()).isNull();
    }

    @Test
    public void save() {
        // given
        User love = newUser("김러브", "love@nate.com", true, LocalDate.now().minusYears(1).minusWeeks(1), 15);

        // when
        User userPS = userRepository.save(love);

        // then (beforeEach에서 2건이 insert 되어 있음)
        Assertions.assertThat(userPS.getId()).isEqualTo(5L);
        Assertions.assertThat(userPS.getUsername()).isEqualTo("김러브");
        Assertions.assertThat(
                passwordEncoder.matches("1234", userPS.getPassword())
        ).isEqualTo(true);
        Assertions.assertThat(userPS.getEmail()).isEqualTo("love@nate.com");
        Assertions.assertThat(userPS.getRole()).isEqualTo(UserRole.ROLE_USER);
        Assertions.assertThat(userPS.getStatus()).isEqualTo(true);
        Assertions.assertThat(userPS.getHireDate()).isEqualTo(LocalDate.now().minusYears(1).minusWeeks(1));
        Assertions.assertThat(userPS.getRemainDays()).isEqualTo(15);
        Assertions.assertThat(userPS.getCreatedAt().toLocalDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(userPS.getUpdatedAt()).isNull();
    }

    @Test
    public void findByHireDate() {
        // given
        LocalDate date = LocalDate.now().minusYears(1);
        int month = LocalDate.now().getMonthValue();
        int day = LocalDate.now().getDayOfMonth();

        // when
        List<User> userPSs = userRepository.findByHireDate(date, month, day);

        // then
        for(int i = 0; i < userPSs.size(); i++){
            User userPS = userPSs.get(i);
            Assertions.assertThat(userPS.getId()).isEqualTo(3L);
            Assertions.assertThat(userPS.getUsername()).isEqualTo("일년차");
            Assertions.assertThat(
                    passwordEncoder.matches("1234", userPS.getPassword())
            ).isEqualTo(true);
            Assertions.assertThat(userPS.getEmail()).isEqualTo("oneyear@nate.com");
            Assertions.assertThat(userPS.getRole()).isEqualTo(UserRole.ROLE_USER);
            Assertions.assertThat(userPS.getStatus()).isEqualTo(true);
            Assertions.assertThat(userPS.getHireDate()).isEqualTo(LocalDate.now().minusYears(1));
            Assertions.assertThat(userPS.getRemainDays()).isEqualTo(0);
            Assertions.assertThat(userPS.getCreatedAt().toLocalDate()).isEqualTo(LocalDate.now());
            Assertions.assertThat(userPS.getUpdatedAt()).isNull();
        }
    }

    @Test
    public void findNewByHireDate() {
        // given
        LocalDate date = LocalDate.now().minusYears(1);
        int day = LocalDate.now().getDayOfMonth();

        // when
        List<User> userPSs = userRepository.findNewByHireDate(date, day);

        // then
        for(int i = 0; i < userPSs.size(); i++){
            User userPS = userPSs.get(i);
            Assertions.assertThat(userPS.getId()).isEqualTo(4L);
            Assertions.assertThat(userPS.getUsername()).isEqualTo("김신입");
            Assertions.assertThat(
                    passwordEncoder.matches("1234", userPS.getPassword())
            ).isEqualTo(true);
            Assertions.assertThat(userPS.getEmail()).isEqualTo("newcomer@nate.com");
            Assertions.assertThat(userPS.getRole()).isEqualTo(UserRole.ROLE_USER);
            Assertions.assertThat(userPS.getStatus()).isEqualTo(true);
            Assertions.assertThat(userPS.getHireDate()).isEqualTo(LocalDate.now().minusMonths(2));
            Assertions.assertThat(userPS.getRemainDays()).isEqualTo(1);
            Assertions.assertThat(userPS.getCreatedAt().toLocalDate()).isEqualTo(LocalDate.now());
            Assertions.assertThat(userPS.getUpdatedAt()).isNull();
        }
    }
}
