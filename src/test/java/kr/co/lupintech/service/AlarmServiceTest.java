package kr.co.lupintech.service;

import kr.co.lupintech.model.alarm.Alarm;
import kr.co.lupintech.model.user.User;
import kr.co.lupintech.model.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;
import kr.co.lupintech.core.dummy.DummyEntity;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;

@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AlarmServiceTest {
    @Autowired
    private AlarmService alarmService;

    @Autowired
    private UserRepository userRepository;
    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private EntityManager em;

    private User user;

    @BeforeEach
    public void setUp() {
        user = userRepository.save(dummy.newUser("nas", true, LocalDate.now().minusYears(1).minusWeeks(1), 15));
        em.clear();
    }

    @DisplayName("알람 DB저장")
    @Test
    public void saveAlarmTest() {
        // given
        Alarm alarm = Alarm.builder()
                .user(user)
                .content("등록되었습니다")
                .build();

        // when
        Alarm result = alarmService.save(alarm);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(alarm.getId(), result.getId());
        Assertions.assertEquals(alarm.getUser().getId(), result.getUser().getId());
        Assertions.assertEquals(alarm.getContent(), result.getContent());
    }
}
