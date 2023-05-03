package shop.mtcoding.restend.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import shop.mtcoding.restend.model.alarm.Alarm;

import shop.mtcoding.restend.model.user.User;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AlarmServiceTest {
    @Autowired
    private AlarmService alarmService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .build();
    }

    @DisplayName("알람 DB저장 성공")
    @Test
    public void saveAlarmTest() {
        // given
        Alarm origin = Alarm.builder()
                .id(1L)
                .user(user)
                .content("등록되었습니다")
                .build();

        // when
        Alarm result = alarmService.save(Alarm.builder().user(user).content("등록되었습니다").build());

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(origin.getId(), result.getId());
        Assertions.assertEquals(origin.getUser().getId(), result.getUser().getId());
        Assertions.assertEquals(origin.getContent(), result.getContent());
    }
}
