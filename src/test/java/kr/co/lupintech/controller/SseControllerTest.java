package kr.co.lupintech.controller;

import kr.co.lupintech.core.MyRestDoc;
import kr.co.lupintech.core.MyWithMockUser;
import kr.co.lupintech.core.auth.session.MyUserDetails;
import kr.co.lupintech.core.dummy.DummyEntity;
import kr.co.lupintech.model.user.User;
import kr.co.lupintech.model.user.UserRepository;
import kr.co.lupintech.model.user.UserRole;
import kr.co.lupintech.service.SseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Objects;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("SSE API")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class SseControllerTest extends MyRestDoc {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private SseService sseService;

    @Autowired
    private UserRepository userRepository;

    private static DummyEntity dummy = new DummyEntity();

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setup() {

        userRepository.save(dummy.newUser("con", true, LocalDate.now().minusYears(1).minusWeeks(1), 15));
        userRepository.save(dummy.newUser("discon", true, LocalDate.now().minusYears(1).minusWeeks(1), 15));

        em.clear();
    }

    @DisplayName("SSE 연결")
    @WithUserDetails(value = "con@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void testConnect() throws Exception {

        SseEmitter emitter = new SseEmitter();

        sseService.add(1L);

        ResultActions resultActions = mvc.perform(get("/auth/connect"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM_VALUE));

        String result = resultActions.andReturn().getResponse().getContentAsString();
        Assertions.assertTrue(result.contains("You are connected"));

        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    @DisplayName("SSE 연결 실패 (인증되지 않은 사용자)")
    @Test
    public void testConnectFailUnauthenticatedTest() throws Exception {
        // when
        ResultActions resultActions = mvc.perform(get("/auth/connect"));

        // then
        resultActions.andExpect(status().isUnauthorized());
        resultActions.andExpect(jsonPath("$.status").value(401));
        resultActions.andExpect(jsonPath("$.msg").value("unAuthorized"));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }


    @DisplayName("SSE 연결 해제")
    @WithUserDetails(value = "discon@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void testDisconnect() throws Exception {
        // Given that the user is connected

        sseService.add(2L);

        // When the user requests to disconnect
        ResultActions resultActions = mvc.perform(post("/auth/disconnect"))
                .andExpect(status().isOk());

        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("SSE 연결 해제 실패 (연결되지 않은 유저)")
    @WithUserDetails(value = "discon@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void testDisconnectFailNotConnectedTest() throws Exception {
        // Given that the user is not connected

        // When the user requests to disconnect
        ResultActions resultActions = mvc.perform(post("/auth/disconnect"));

        // then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("id"));
        resultActions.andExpect(jsonPath("$.data.value").value("연결되지 않은 유저입니다."));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

}