package shop.mtcoding.restend.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import shop.mtcoding.restend.core.MyRestDoc;
import shop.mtcoding.restend.core.dummy.DummyEntity;
import shop.mtcoding.restend.model.alarm.Alarm;

import shop.mtcoding.restend.model.user.User;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.restend.model.user.UserRepository;
import shop.mtcoding.restend.model.user.UserRole;

import javax.persistence.EntityManager;

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
        user = userRepository.save(dummy.newUser("nas", true, 15));
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
