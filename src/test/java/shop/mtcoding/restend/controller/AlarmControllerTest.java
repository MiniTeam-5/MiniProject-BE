package shop.mtcoding.restend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import shop.mtcoding.restend.core.dummy.DummyEntity;
import shop.mtcoding.restend.model.alarm.Alarm;
import shop.mtcoding.restend.model.alarm.AlarmRepository;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;
import shop.mtcoding.restend.model.user.UserRole;


import javax.persistence.EntityManager;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("알람 API")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AlarmControllerTest {

    private DummyEntity dummy = new DummyEntity();
    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private EntityManager em;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        userRepository.save(dummy.newUser("ssar", true, LocalDate.now().minusYears(1).minusWeeks(1), 15));
        userRepository.save(dummy.newUser("cos", true, LocalDate.now().minusYears(1).minusWeeks(1), 15));
        user = userRepository.save(dummy.newUser("dotori", true, LocalDate.now().minusYears(1).minusWeeks(1), 15));
        em.clear();
    }

    @DisplayName("알람 불러오기")
    @WithUserDetails(value = "dotori@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void getUserAlarmsTest() throws Exception {
        // given

        Alarm alarm1 = Alarm.builder().id(1L).user(user).content("알람1").build();
        Alarm alarm2 = Alarm.builder().id(2L).user(user).content("알람2").build();
        alarmRepository.save(alarm1);
        alarmRepository.save(alarm2);

        em.clear();

        // when
        ResultActions resultActions = mvc.perform(get("/auth/alarm"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(alarm1.getId().intValue()))
                .andExpect(jsonPath("$.data[0].content").value(alarm1.getContent()))
                .andExpect(jsonPath("$.data[1].id").value(alarm2.getId().intValue()))
                .andExpect(jsonPath("$.data[1].content").value(alarm2.getContent()));
    }
}
