package kr.co.lupintech.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.lupintech.core.MyWithMockUser;
import kr.co.lupintech.core.advice.MyLogAdvice;
import kr.co.lupintech.core.advice.MyValidAdvice;
import kr.co.lupintech.core.config.MyFilterRegisterConfig;
import kr.co.lupintech.core.config.MySecurityConfig;
import kr.co.lupintech.dto.leave.LeaveRequest;
import kr.co.lupintech.dto.leave.LeaveResponse;
import kr.co.lupintech.model.leave.enums.LeaveStatus;
import kr.co.lupintech.model.leave.enums.LeaveType;
import kr.co.lupintech.model.user.User;
import kr.co.lupintech.model.user.UserRole;
import kr.co.lupintech.service.LeaveService;
import kr.co.lupintech.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import kr.co.lupintech.core.dummy.DummyEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@ActiveProfiles("test")
@EnableAspectJAutoProxy // AOP 활성화
@Import({
        MyValidAdvice.class,
        MyLogAdvice.class,
        MySecurityConfig.class,
        MyFilterRegisterConfig.class
}) // Advice 와 Security 설정 가져오기
@WebMvcTest(
        // 필요한 Controller 가져오기, 특정 필터를 제외하기
        controllers = {AdminController.class}
)
public class AdminControllerUnitTest extends DummyEntity{
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @MockBean
    private UserService userService;
    @MockBean
    private LeaveService leaveService;

    @MyWithMockUser(id = 1L, username = "박코스", role = UserRole.ROLE_ADMIN)
    @Test
    public void getLeave_test() throws Exception {
        // given
        LeaveResponse.InfoOutDTO leaveInfo1 = new LeaveResponse.InfoOutDTO(1L, 1L,
                "일유저",
                LeaveType.DUTY,
                LeaveStatus.WAITING,
                LocalDateTime.now().toString(),
                LocalDateTime.now().plusDays(3).toString(),
                "https://lupinbucket.s3.ap-northeast-2.amazonaws.com/person.png");
        LeaveResponse.InfoOutDTO leaveInfo2 = new LeaveResponse.InfoOutDTO(2L, 2L,
                "이유저",
                LeaveType.ANNUAL,
                LeaveStatus.WAITING,
                LocalDateTime.now().toString(),
                LocalDateTime.now().plusDays(2).toString(),
                "https://lupinbucket.s3.ap-northeast-2.amazonaws.com/person.png");
        List<LeaveResponse.InfoOutDTO> waitingLeaves = Arrays.asList(leaveInfo1, leaveInfo2);

        Mockito.when(leaveService.상태선택연차당직정보가져오기(LeaveStatus.WAITING)).thenReturn(waitingLeaves);

        // when
        ResultActions resultActions = mvc.perform(get("/admin/leave").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // then
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        resultActions.andExpect(jsonPath("$.data", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].userId").value(1L))
                .andExpect(jsonPath("$.data[0].username").value("일유저"))
                .andExpect(jsonPath("$.data[0].type").value("DUTY"))
                .andExpect(jsonPath("$.data[0].status").value("WAITING"))
                .andExpect(jsonPath("$.data[1].id").value(2L))
                .andExpect(jsonPath("$.data[1].userId").value(2L))
                .andExpect(jsonPath("$.data[1].username").value("이유저"))
                .andExpect(jsonPath("$.data[1].type").value("ANNUAL"))
                .andExpect(jsonPath("$.data[1].status").value("WAITING"));
    }

    @WithMockUser(username="admin@nate.com", roles={"ADMIN"})
    @Test
    public void decide_test() throws Exception {
        // given
        LeaveRequest.DecideInDTO decideInDTO = new LeaveRequest.DecideInDTO();
        decideInDTO.setId(1L);
        decideInDTO.setStatus(LeaveStatus.APPROVAL);
        String requestBody = om.writeValueAsString(decideInDTO);

        // stub
        User user = newMockUser(1L, "박코스", "cos@nate.com",14);
        LeaveResponse.DecideOutDTO decideOutDTO = new LeaveResponse.DecideOutDTO(user);
        Mockito.when(leaveService.연차당직결정하기(any())).thenReturn(decideOutDTO);

        // shen
        ResultActions resultActions = mvc
                .perform(post("/admin/approve").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // 검증해볼께
        resultActions.andExpect(jsonPath("$.data.remainDays").value(14));
    }

}
