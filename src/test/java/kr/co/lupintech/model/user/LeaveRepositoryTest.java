package kr.co.lupintech.model.user;

import kr.co.lupintech.core.exception.Exception400;
import kr.co.lupintech.model.leave.Leave;
import kr.co.lupintech.model.leave.LeaveRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import kr.co.lupintech.core.dummy.DummyEntity;
import kr.co.lupintech.model.leave.enums.LeaveStatus;
import kr.co.lupintech.model.leave.enums.LeaveType;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Import(BCryptPasswordEncoder.class)
@ActiveProfiles("test")
@DataJpaTest
public class LeaveRepositoryTest extends DummyEntity {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LeaveRepository leaveRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE leave_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        userRepository.save(newUser("김쌀쌀", "ssar@nate.com", true, LocalDate.now().minusYears(1).minusWeeks(1), 15));
        User ssar = userRepository.findById(1L).orElseThrow(
                () -> new RuntimeException("테스트 중 findById 에러 : 1번 유저가 없습니다")
        );;
        leaveRepository.save(newLeave(ssar, LeaveType.DUTY, LocalDate.parse("2023-07-25"), LocalDate.parse("2023-07-25"),0, LeaveStatus.WAITING));
        em.clear();
    }

    @Test
    public void save() {
        // given
        userRepository.save(newUser("박코스", "cos@nate.com", true, LocalDate.now().minusYears(1).minusWeeks(1), 15));
        User cos = userRepository.findById(2L).orElseThrow(
                () -> new RuntimeException("테스트 중 findById 에러 : 2번 유저가 없습니다")
        );
        Leave leave = newLeave(cos, LeaveType.DUTY, LocalDate.parse("2023-07-26"), LocalDate.parse("2023-07-26"), 0, LeaveStatus.WAITING);

        // when
        Leave leavePS = leaveRepository.save(leave);

        // then (beforeEach에서 1건이 insert 되어 있음)
        Assertions.assertThat(leavePS.getId()).isEqualTo(2L);
        Assertions.assertThat(leavePS.getUser()).isEqualTo(cos);
        Assertions.assertThat(leavePS.getType()).isEqualTo(LeaveType.DUTY);
        Assertions.assertThat(leavePS.getStartDate()).isEqualTo(LocalDate.parse("2023-07-26"));
        Assertions.assertThat(leavePS.getEndDate()).isEqualTo(LocalDate.parse("2023-07-26"));
        Assertions.assertThat(leavePS.getUsingDays()).isEqualTo(0);
        Assertions.assertThat(leavePS.getStatus()).isEqualTo(LeaveStatus.WAITING);
        Assertions.assertThat(leavePS.getCreatedAt().toLocalDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(leavePS.getUpdatedAt()).isNull();
    }

    @Test
    public void findById() {
        // given
        Long id = 1L;

        // when
        Optional<Leave> leaveOP = leaveRepository.findById(id);
        if (leaveOP.isEmpty()) {
            throw new Exception400("id", "아이디를 찾을 수 없습니다");
        }
        Leave leavePS = leaveOP.get();

        User ssar = userRepository.findById(1L).orElseThrow(
                () -> new RuntimeException("1번 유저가 없습니다")
        );

        // then
        Assertions.assertThat(leavePS.getId()).isEqualTo(1L);
        Assertions.assertThat(leavePS.getUser()).isEqualTo(ssar);
        Assertions.assertThat(leavePS.getType()).isEqualTo(LeaveType.DUTY);
        Assertions.assertThat(leavePS.getStartDate()).isEqualTo(LocalDate.parse("2023-07-25"));
        Assertions.assertThat(leavePS.getEndDate()).isEqualTo(LocalDate.parse("2023-07-25"));
        Assertions.assertThat(leavePS.getUsingDays()).isEqualTo(0);
        Assertions.assertThat(leavePS.getStatus()).isEqualTo(LeaveStatus.WAITING);
        Assertions.assertThat(leavePS.getCreatedAt().toLocalDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(leavePS.getUpdatedAt()).isNull();
    }

    @Test
    public void delete() {
        // given
        Long id = 1L;

        // when
        Leave leavePS = leaveRepository.findById(id).orElseThrow(
                () -> new Exception400("id", "아이디를 찾을 수 없습니다")
        );
        leaveRepository.delete(leavePS);
        Optional<Leave> leaveOP = leaveRepository.findById(id);

        // then
        Assertions.assertThat(leaveOP).isEmpty();
    }

    @Test
    public void findByStartDateAndStatus() {
        // given
        Long id = 1L;
        LocalDate today = LocalDate.parse("2023-07-25");

        // when
        List<Leave> leavePSs = leaveRepository.findByStartDateAndStatus(today, LeaveStatus.WAITING);

        // then
        for(int i = 0; i < leavePSs.size(); i++){
            Leave leavePS = leavePSs.get(i);
            Assertions.assertThat(leavePS.getId()).isEqualTo(1L);
            Assertions.assertThat(leavePS.getType()).isEqualTo(LeaveType.DUTY);
            Assertions.assertThat(leavePS.getStartDate()).isEqualTo(LocalDate.parse("2023-07-25"));
            Assertions.assertThat(leavePS.getEndDate()).isEqualTo(LocalDate.parse("2023-07-25"));
            Assertions.assertThat(leavePS.getUsingDays()).isEqualTo(0);
            Assertions.assertThat(leavePS.getStatus()).isEqualTo(LeaveStatus.WAITING);
            Assertions.assertThat(leavePS.getCreatedAt().toLocalDate()).isEqualTo(LocalDate.now());
            Assertions.assertThat(leavePS.getUpdatedAt()).isNull();

            User userPS = leavePSs.get(i).getUser();
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
    }

    @Test
    public void existsDuplicateDuty(){
        // given
        userRepository.save(newUser("박코스", "cos@nate.com", true, LocalDate.now().minusYears(1).minusWeeks(1), 15));
        User cos = userRepository.findById(2L).orElseThrow(
                () -> new RuntimeException("테스트 중 findById 에러 : 2번 유저가 없습니다")
        );
        leaveRepository.save(newLeave(cos, LeaveType.DUTY, LocalDate.parse("2023-08-01"),
                LocalDate.parse("2023-08-01"), 0, LeaveStatus.WAITING));

        LeaveType type = LeaveType.DUTY;
        LocalDate startDate = LocalDate.parse("2023-08-01");
        Long userId = 2L;

        // when
        boolean result = leaveRepository.existsDuplicateDuty(type, startDate, userId);

        // then
        Assertions.assertThat(result).isEqualTo(true);
    }

    @Test
    public void existsDuplicateAnnual(){
        // given
        userRepository.save(newUser("박코스", "cos@nate.com", true, LocalDate.now().minusYears(1).minusWeeks(1), 15));
        User cos = userRepository.findById(2L).orElseThrow(
                () -> new RuntimeException("테스트 중 findById 에러 : 2번 유저가 없습니다")
        );
        leaveRepository.save(newLeave(cos, LeaveType.ANNUAL, LocalDate.parse("2023-06-20"),
                LocalDate.parse("2023-06-23"), 0, LeaveStatus.WAITING));

        LeaveType type = LeaveType.ANNUAL;
        LocalDate startDate = LocalDate.parse("2023-06-19");
        LocalDate endDate = LocalDate.parse("2023-06-21");
        Long userId = 2L;

        // when
        boolean result = leaveRepository.existsDuplicateAnnual(type, startDate, endDate, userId);

        // then
        Assertions.assertThat(result).isEqualTo(true);
    }
}
