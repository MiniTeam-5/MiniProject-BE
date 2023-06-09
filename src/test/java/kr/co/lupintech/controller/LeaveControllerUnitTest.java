package kr.co.lupintech.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.lupintech.core.MyWithMockUser;
import kr.co.lupintech.core.advice.MyLogAdvice;
import kr.co.lupintech.core.advice.MyValidAdvice;
import kr.co.lupintech.core.auth.session.MyUserDetails;
import kr.co.lupintech.core.config.MyFilterRegisterConfig;
import kr.co.lupintech.core.config.MySecurityConfig;
import kr.co.lupintech.dto.leave.LeaveRequest;
import kr.co.lupintech.dto.leave.LeaveResponse;
import kr.co.lupintech.model.leave.Leave;
import kr.co.lupintech.model.leave.enums.LeaveStatus;
import kr.co.lupintech.model.leave.enums.LeaveType;
import kr.co.lupintech.model.user.User;
import kr.co.lupintech.model.user.UserRole;
import kr.co.lupintech.service.LeaveService;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @WebMvcTest는 웹 계층 컴포넌트만 테스트로 가져옴
 */

@ActiveProfiles("test")
@EnableAspectJAutoProxy // AOP 활성화
@Import({
        MyValidAdvice.class,
        MyLogAdvice.class,
        MySecurityConfig.class,
        MyFilterRegisterConfig.class,
        MyUserDetails.class
}) // Advice 와 Security 설정 가져오기
@WebMvcTest(
        // 필요한 Controller 가져오기, 특정 필터를 제외하기
        controllers = {LeaveController.class}
)
public class LeaveControllerUnitTest extends DummyEntity {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @MockBean
    private LeaveService leaveService;
    @MockBean
    private MyUserDetails myUserDetails;

    @MyWithMockUser(id = 1L, username = "박코스", role = UserRole.ROLE_USER, remainDays = 15)
    @Test
    public void apply_test() throws Exception {
        // 준비
        LeaveRequest.ApplyInDTO applyInDTO = new LeaveRequest.ApplyInDTO();
        applyInDTO.setType(LeaveType.valueOf("ANNUAL"));
        applyInDTO.setStartDate(LocalDate.parse("2023-07-20"));
        applyInDTO.setEndDate(LocalDate.parse("2023-07-20"));
        String requestBody = om.writeValueAsString(applyInDTO);

        // 가정해볼께
        User user = newMockUser(1L,"박코스", "cos@nate.com", 14);
        Leave leave = newMockLeave(1L, user, LeaveType.valueOf("ANNUAL"), LocalDate.parse("2023-07-20"),
                LocalDate.parse("2023-07-20"), 1);
        LeaveResponse.ApplyOutDTO applyOutDTO = new LeaveResponse.ApplyOutDTO(leave, user);
        Mockito.when(leaveService.연차당직신청하기(any(), any())).thenReturn(applyOutDTO);

        // 테스트진행
        ResultActions resultActions = mvc
                .perform(post("/auth/leave/apply").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // 검증해볼께
        resultActions.andExpect(jsonPath("$.data.id").value(1L));
        resultActions.andExpect(jsonPath("$.data.type").value("ANNUAL"));
        resultActions.andExpect(jsonPath("$.data.usingDays").value(1));
        resultActions.andExpect(jsonPath("$.data.remainDays").value(14));
        resultActions.andExpect(jsonPath("$.data.status").value("WAITING"));
        resultActions.andExpect(status().isOk());
    }

    @MyWithMockUser(id = 1L, username = "박코스", role = UserRole.ROLE_USER, remainDays = 15)
    @Test
    public void cancel_test() throws Exception {
        // given
        Long id = 1L;

        // stub
        User user = newMockUser(1L,"박코스", "cos@nate.com", 9);
        LeaveResponse.CancelOutDTO cancelOutDTO = new LeaveResponse.CancelOutDTO(user);
        Mockito.when(leaveService.연차당직신청취소하기(any(), any())).thenReturn(cancelOutDTO);

        // shen
        ResultActions resultActions = mvc
                .perform(post("/auth/leave/"+id+"/delete"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // 검증해볼께
        resultActions.andExpect(jsonPath("$.data.remainDays").value(9));
        resultActions.andExpect(status().isOk());
    }

    @MyWithMockUser(id = 1L, username = "박코스", role = UserRole.ROLE_USER, remainDays = 15)
    @Test
    public void getLeaveData_test() throws Exception {
        // 준비
        User user = newMockUser(1L, "박코스", "cos@nate.com",14);
        Leave leave = newMockLeave(1L, user, LeaveType.valueOf("ANNUAL"), LocalDate.parse("2023-07-27"),
                LocalDate.parse("2023-08-02"), 5);
        LeaveResponse.InfoOutDTO infoOutDTO = new LeaveResponse.InfoOutDTO(leave, user);

        Leave leave2 = newMockLeave(1L, user, LeaveType.valueOf("ANNUAL"), LocalDate.parse("2023-05-27"),
                LocalDate.parse("2023-06-02"), 5);
        LeaveResponse.InfoOutDTO infoOutDTO2 = new LeaveResponse.InfoOutDTO(leave2, user);

        Leave leave3 = newMockLeave(1L, user, LeaveType.valueOf("ANNUAL"), LocalDate.parse("2023-08-27"),
                LocalDate.parse("2023-09-02"), 5);
        LeaveResponse.InfoOutDTO infoOutDTO3 = new LeaveResponse.InfoOutDTO(leave3, user);

        List<LeaveResponse.InfoOutDTO> infoOutDTOList = new ArrayList<>();
        infoOutDTOList.add(infoOutDTO);
        infoOutDTOList.add(infoOutDTO2);
        infoOutDTOList.add(infoOutDTO3);

        // 가정
        Mockito.when(leaveService.연차당직정보가져오기세달치(any())).thenReturn(infoOutDTOList);

        String month = "2023-07";
        // 테스트 진행
        ResultActions resultActions = mvc.perform(get("/auth/leave/month/" + month)
                .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // 검증
        resultActions.andExpect(jsonPath("$.data[0].userId").value(1L));
        resultActions.andExpect(jsonPath("$.data[0].username").value("박코스"));
        resultActions.andExpect(jsonPath("$.data[0].type").value("ANNUAL"));
        resultActions.andExpect(jsonPath("$.data[0].status").value("WAITING"));
        resultActions.andExpect(jsonPath("$.data[0].startDate").value("2023-07-27"));
        resultActions.andExpect(jsonPath("$.data[0].endDate").value("2023-08-02"));
        resultActions.andExpect(jsonPath("$.data[1].startDate").value("2023-05-27"));
        resultActions.andExpect(jsonPath("$.data[1].endDate").value("2023-06-02"));
        resultActions.andExpect(jsonPath("$.data[2].startDate").value("2023-08-27"));
        resultActions.andExpect(jsonPath("$.data[2].endDate").value("2023-09-02"));
        resultActions.andExpect(status().isOk());
    }

    @MyWithMockUser(id = 1L, username = "박코스", role = UserRole.ROLE_USER, remainDays = 15)
    @Test
    public void getById_test() throws Exception {
        // 준비
        Long id = 1L;
        User user = newMockUser(1L, "박코스", "cos@nate.com", 14);
        Leave leave = newMockLeave(1L, user, LeaveType.valueOf("ANNUAL"), LocalDate.parse("2023-07-20"),
                LocalDate.parse("2023-07-21"), 2);
        LeaveResponse.InfoOutDTO infoOutDTO = new LeaveResponse.InfoOutDTO(leave, user);

        List<LeaveResponse.InfoOutDTO> infoOutDTOList = new ArrayList<>();
        infoOutDTOList.add(infoOutDTO);

        // 가정
        Mockito.when(leaveService.특정유저연차당직정보가져오기(id)).thenReturn(infoOutDTOList);

        // 테스트 진행
        ResultActions resultActions = mvc.perform(get("/auth/leave/id/" + id)
                .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // 검증
        resultActions.andExpect(jsonPath("$.data[0].userId").value(1L));
        resultActions.andExpect(jsonPath("$.data[0].username").value("박코스"));
        resultActions.andExpect(jsonPath("$.data[0].type").value("ANNUAL"));
        resultActions.andExpect(jsonPath("$.data[0].status").value("WAITING"));
        resultActions.andExpect(jsonPath("$.data[0].startDate").value("2023-07-20"));
        resultActions.andExpect(jsonPath("$.data[0].endDate").value("2023-07-21"));
        resultActions.andExpect(status().isOk());
    }
}

