package shop.mtcoding.restend.model.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import shop.mtcoding.restend.core.dummy.DummyEntity;
import shop.mtcoding.restend.core.exception.Exception400;
import shop.mtcoding.restend.model.leave.Leave;
import shop.mtcoding.restend.model.leave.LeaveRepository;
import shop.mtcoding.restend.model.leave.enums.LeaveStatus;
import shop.mtcoding.restend.model.leave.enums.LeaveType;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Optional;

@ActiveProfiles("test")
@DataJpaTest
public class LeaveRepositoryTest extends DummyEntity {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LeaveRepository leaveRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE leave_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        userRepository.save(newUser("ssar", 15));
        User ssar = userRepository.findById(1L).orElseThrow(
                () -> new RuntimeException("테스트 중 findById 에러 : 1번 유저가 없습니다")
        );;
        leaveRepository.save(newLeave(ssar, LeaveType.DUTY, LocalDate.parse("2023-07-25"), LocalDate.parse("2023-07-25"),0, LeaveStatus.WAITING));
        em.clear();
    }

    @Test
    public void save() {
        // given
        userRepository.save(newUser("cos", 15));
        User cos = userRepository.findById(2L).orElseThrow(
                () -> new RuntimeException("테스트 중 findById 에러 : 2번 유저가 없습니다")
        );;
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
        );;

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
}
