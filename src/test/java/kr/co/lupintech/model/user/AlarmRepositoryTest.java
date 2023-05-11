package kr.co.lupintech.model.user;

import kr.co.lupintech.model.alarm.Alarm;
import kr.co.lupintech.model.alarm.AlarmRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import kr.co.lupintech.core.dummy.DummyEntity;

import javax.persistence.EntityManager;
import java.time.LocalDate;

@ActiveProfiles("test")
@DataJpaTest
public class AlarmRepositoryTest extends DummyEntity {

    @Autowired
    private AlarmRepository alarmRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE alarm_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        userRepository.save(newUser("ssar", true, LocalDate.now().minusYears(1).minusWeeks(1), 15));
        User ssar = userRepository.findById(1L).orElseThrow(
                () -> new RuntimeException("테스트 중 findById 에러 : 1번 유저가 없습니다")
        );;
        alarmRepository.save(newAlarm(ssar, "첫번째 알람 메세지"));
        em.clear();
    }
    @Test
    public void save() {
        // given
        userRepository.save(newUser("cos", true, LocalDate.now().minusYears(1).minusWeeks(1), 15));
        User cos = userRepository.findById(2L).orElseThrow(
                () -> new RuntimeException("테스트 중 findById 에러 : 2번 유저가 없습니다")
        );;
        Alarm alarm = newAlarm(cos, "추가한 알람 메세지");

        // when
        Alarm alarmPS = alarmRepository.save(alarm);

        // then (beforeEach에서 1건이 insert 되어 있음)
        Assertions.assertThat(alarmPS.getId()).isEqualTo(2L);
        Assertions.assertThat(alarmPS.getUser()).isEqualTo(cos);
        Assertions.assertThat(alarmPS.getContent()).isEqualTo("추가한 알람 메세지");
        Assertions.assertThat(alarmPS.getCreatedAt().toLocalDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(alarmPS.getUpdatedAt()).isNull();
    }
}
