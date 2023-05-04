package shop.mtcoding.restend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import shop.mtcoding.restend.core.MyRestDoc;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.core.dummy.DummyEntity;
import shop.mtcoding.restend.dto.leave.LeaveRequest;
import shop.mtcoding.restend.model.leave.Leave;
import shop.mtcoding.restend.model.leave.LeaveRepository;
import shop.mtcoding.restend.model.leave.enums.LeaveStatus;
import shop.mtcoding.restend.model.leave.enums.LeaveType;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("연차/당직 API")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class LeaveControllerTest extends MyRestDoc {

    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LeaveRepository leaveRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        User ssar = userRepository.save(dummy.newUser("ssar", 15));
        User cos = userRepository.save(dummy.newUser("cos", 11));
        leaveRepository.save(dummy.newLeave(ssar, LeaveType.ANNUAL, LocalDate.parse("2023-08-19"),
                LocalDate.parse("2023-08-19"), 1, LeaveStatus.REJECTION));
        leaveRepository.save(dummy.newLeave(cos, LeaveType.ANNUAL, LocalDate.parse("2023-08-10"),
                LocalDate.parse("2023-08-11"), 2, LeaveStatus.APPROVAL));
        leaveRepository.save(dummy.newLeave(ssar, LeaveType.DUTY, LocalDate.parse("2023-07-10"),
                LocalDate.parse("2023-07-10"), 0, LeaveStatus.WAITING));
        leaveRepository.save(dummy.newLeave(cos, LeaveType.ANNUAL, LocalDate.parse("2023-09-18"),
                LocalDate.parse("2023-09-19"), 2, LeaveStatus.WAITING));
        em.clear();
    }

    @DisplayName("연차 신청 성공 (하루)")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void apply_annual_one_day_test() throws Exception {
        // given
        LeaveRequest.ApplyInDTO applyInDTO = new LeaveRequest.ApplyInDTO();
        applyInDTO.setType(LeaveType.valueOf("ANNUAL"));
        applyInDTO.setStartDate(LocalDate.parse("2023-07-20"));
        applyInDTO.setEndDate(LocalDate.parse("2023-07-20"));
        String requestBody = om.writeValueAsString(applyInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/leave/apply").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.id").value(9L));
        resultActions.andExpect(jsonPath("$.data.type").value("ANNUAL"));
        resultActions.andExpect(jsonPath("$.data.usingDays").value(1));
        resultActions.andExpect(jsonPath("$.data.remainDays").value(14));
        resultActions.andExpect(jsonPath("$.data.status").value("WAITING"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("연차 신청 성공 (여러 일)")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void apply_annual_many_days_test() throws Exception {
        // given
        LeaveRequest.ApplyInDTO applyInDTO = new LeaveRequest.ApplyInDTO();
        applyInDTO.setType(LeaveType.valueOf("ANNUAL"));
        applyInDTO.setStartDate(LocalDate.parse("2023-07-19"));
        applyInDTO.setEndDate(LocalDate.parse("2023-07-21"));
        String requestBody = om.writeValueAsString(applyInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/leave/apply").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.id").value(40L));
        resultActions.andExpect(jsonPath("$.data.type").value("ANNUAL"));
        resultActions.andExpect(jsonPath("$.data.usingDays").value(3));
        resultActions.andExpect(jsonPath("$.data.remainDays").value(12));
        resultActions.andExpect(jsonPath("$.data.status").value("WAITING"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("연차 신청 성공 (주말 포함)")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void apply_annual_including_weekend_test() throws Exception {
        // given
        LeaveRequest.ApplyInDTO applyInDTO = new LeaveRequest.ApplyInDTO();
        applyInDTO.setType(LeaveType.valueOf("ANNUAL"));
        applyInDTO.setStartDate(LocalDate.parse("2023-07-21"));
        applyInDTO.setEndDate(LocalDate.parse("2023-07-25"));
        String requestBody = om.writeValueAsString(applyInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/leave/apply").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.id").value(23L));
        resultActions.andExpect(jsonPath("$.data.type").value("ANNUAL"));
        resultActions.andExpect(jsonPath("$.data.usingDays").value(3));
        resultActions.andExpect(jsonPath("$.data.remainDays").value(12));
        resultActions.andExpect(jsonPath("$.data.status").value("WAITING"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("연차 신청 성공 (주말 포함)")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void apply_duty_test() throws Exception {
        // given
        LeaveRequest.ApplyInDTO applyInDTO = new LeaveRequest.ApplyInDTO();
        applyInDTO.setType(LeaveType.valueOf("DUTY"));
        applyInDTO.setStartDate(LocalDate.parse("2023-07-28"));
        applyInDTO.setEndDate(LocalDate.parse("2023-07-28"));
        String requestBody = om.writeValueAsString(applyInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/leave/apply").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.id").value(14L));
        resultActions.andExpect(jsonPath("$.data.type").value("DUTY"));
        resultActions.andExpect(jsonPath("$.data.usingDays").value(0));
        resultActions.andExpect(jsonPath("$.data.remainDays").value(15));
        resultActions.andExpect(jsonPath("$.data.status").value("WAITING"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("당직 신청 실패 (하루만 신청해야 함)")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void apply_duty_fail_not_one_day_test() throws Exception {
        // given
        LeaveRequest.ApplyInDTO applyInDTO = new LeaveRequest.ApplyInDTO();
        applyInDTO.setType(LeaveType.valueOf("DUTY"));
        applyInDTO.setStartDate(LocalDate.parse("2023-07-27"));
        applyInDTO.setEndDate(LocalDate.parse("2023-07-28"));
        String requestBody = om.writeValueAsString(applyInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/leave/apply").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("startDate, endDate"));
        resultActions.andExpect(jsonPath("$.data.value").value("startDate와 endDate가 같아야 합니다."));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("연차/당직 신청 유효성 검사 실패")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void apply_fail_valid_test() throws Exception {
        // given
        LeaveRequest.ApplyInDTO applyInDTO = new LeaveRequest.ApplyInDTO();
        applyInDTO.setType(LeaveType.valueOf("ANNUAL"));
        applyInDTO.setStartDate(LocalDate.parse("2022-07-24"));
        applyInDTO.setEndDate(LocalDate.parse("2024-07-24"));
        String requestBody = om.writeValueAsString(applyInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/leave/apply").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("startDate"));
        resultActions.andExpect(jsonPath("$.data.value").value("must be a date in the present or in the future"));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("연차 신청 실패 (0일 신청)")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void apply_annual_fail_zero_day_test() throws Exception {
        // given
        LeaveRequest.ApplyInDTO applyInDTO = new LeaveRequest.ApplyInDTO();
        applyInDTO.setType(LeaveType.valueOf("ANNUAL"));
        applyInDTO.setStartDate(LocalDate.parse("2023-07-22"));
        applyInDTO.setEndDate(LocalDate.parse("2023-07-23"));
        String requestBody = om.writeValueAsString(applyInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/leave/apply").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("startDate, endDate"));
        resultActions.andExpect(jsonPath("$.data.value").value("연차를 0일 신청했습니다."));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
    @DisplayName("연차 신청 실패 (남은 연차 일수보다 많이 신청)")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void apply_annual_fail_over_day_test() throws Exception {
        // given
        LeaveRequest.ApplyInDTO applyInDTO = new LeaveRequest.ApplyInDTO();
        applyInDTO.setType(LeaveType.valueOf("ANNUAL"));
        applyInDTO.setStartDate(LocalDate.parse("2023-07-21"));
        applyInDTO.setEndDate(LocalDate.parse("2023-09-28"));
        String requestBody = om.writeValueAsString(applyInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/leave/apply").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("startDate, endDate"));
        resultActions.andExpect(jsonPath("$.data.value").value("남은 연차보다 더 많이 신청했습니다."));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("연차 신청 취소 성공")
    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void cancel_annual_test() throws Exception {
        // given
        Long id = 4L;

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/leave/"+id+"/delete"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.remainDays").value(13));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("당직 신청 취소 성공")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void cancel_duty_test() throws Exception {
        // given
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        authentication.getPrincipal().
//        MyUserDetails myUserDetails
//        leaveRepository.save(dummy.newLeave(lov, LeaveType.ANNUAL, LocalDate.parse("2023-08-10"),
//                LocalDate.parse("2023-08-11"), 2, LeaveStatus.APPROVAL));
        Long id = 3L;

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/leave/"+id+"/delete"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.remainDays").value(15));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("연차/당직 신청 취소 유효성 검사 실패 (연차/당직 신청 정보가 없을 때)")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void cancel_fail_valid_no_leave_test() throws Exception {
        // given
        Long id = 200L;

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/leave/"+id+"/delete"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(500));
        resultActions.andExpect(jsonPath("$.msg").value("serverError"));
        resultActions.andExpect(jsonPath("$.data").value("해당 연차/당직 신청 정보가 DB에 존재하지 않음"));
        resultActions.andExpect(status().is5xxServerError());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("연차/당직 신청 취소 실패 (이미 승인됨)")
    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void cancel_fail_already_approved_test() throws Exception {
        // given
//        User lov = userRepository.save(dummy.newUser("lov", 13));
//        leaveRepository.save(dummy.newLeave(lov, LeaveType.ANNUAL, LocalDate.parse("2023-08-10"),
//                LocalDate.parse("2023-08-11"), 2, LeaveStatus.APPROVAL));
        Long id = 2L;

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/leave/"+id+"/delete"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("id"));
        resultActions.andExpect(jsonPath("$.data.value").value("이미 승인된 신청입니다."));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("연차/당직 신청 취소 실패 (이미 거절됨)")
    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void cancel_fail_already_rejected_test() throws Exception {
        // given
//        User big = userRepository.save(dummy.newUser("big", 15));
//        leaveRepository.save(dummy.newLeave(big, LeaveType.ANNUAL, LocalDate.parse("2023-08-19"),
//                LocalDate.parse("2023-08-19"), 1, LeaveStatus.REJECTION));
        Long id = 1L;

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/leave/"+id+"/delete"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("id"));
        resultActions.andExpect(jsonPath("$.data.value").value("이미 거절된 신청입니다."));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
