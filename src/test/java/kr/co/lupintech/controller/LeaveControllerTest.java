package kr.co.lupintech.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.lupintech.core.MyRestDoc;
import kr.co.lupintech.dto.leave.LeaveRequest;
import kr.co.lupintech.model.leave.LeaveRepository;
import kr.co.lupintech.model.leave.enums.LeaveStatus;
import kr.co.lupintech.model.leave.enums.LeaveType;
import kr.co.lupintech.model.user.User;
import kr.co.lupintech.model.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import kr.co.lupintech.core.dummy.DummyEntity;

import javax.persistence.EntityManager;

import java.time.LocalDate;

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

    private static DummyEntity dummy = new DummyEntity();

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
        User ssar = userRepository.save(dummy.newUser("김쌀쌀", "ssar@nate.com", true,LocalDate.now().minusYears(1).minusWeeks(1), 13));
        User cos = userRepository.save(dummy.newUser("박코스", "cos@nate.com", true, LocalDate.now().minusYears(1).minusWeeks(1), 11));
        User abort = userRepository.save(dummy.newUser("이삭제", "abort@nate.com", false,  LocalDate.now().minusYears(1).minusWeeks(1), 14));
        leaveRepository.save(dummy.newLeave(ssar, LeaveType.ANNUAL, LocalDate.parse("2023-08-19"),
                LocalDate.parse("2023-08-19"), 1, LeaveStatus.REJECTION));
        leaveRepository.save(dummy.newLeave(cos, LeaveType.ANNUAL, LocalDate.parse("2023-08-10"),
                LocalDate.parse("2023-08-11"), 2, LeaveStatus.APPROVAL));
        leaveRepository.save(dummy.newLeave(ssar, LeaveType.DUTY, LocalDate.parse("2023-07-10"),
                LocalDate.parse("2023-07-10"), 0, LeaveStatus.WAITING));
        leaveRepository.save(dummy.newLeave(cos, LeaveType.ANNUAL, LocalDate.parse("2023-09-18"),
                LocalDate.parse("2023-09-19"), 2, LeaveStatus.WAITING));
        leaveRepository.save(dummy.newLeave(abort, LeaveType.ANNUAL, LocalDate.parse("2023-09-29"),
                LocalDate.parse("2023-09-29"), 1, LeaveStatus.WAITING));
        leaveRepository.save(dummy.newLeave(ssar, LeaveType.ANNUAL, LocalDate.parse("2023-07-17"),
                LocalDate.parse("2023-07-18"), 2, LeaveStatus.WAITING));
        leaveRepository.save(dummy.newLeave(cos, LeaveType.ANNUAL, LocalDate.parse("2023-10-26"),
                LocalDate.parse("2023-10-27"), 2, LeaveStatus.WAITING));
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
        resultActions.andExpect(jsonPath("$.data.id").value(37L));
        resultActions.andExpect(jsonPath("$.data.type").value("ANNUAL"));
        resultActions.andExpect(jsonPath("$.data.usingDays").value(1));
        resultActions.andExpect(jsonPath("$.data.remainDays").value(12));
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
        applyInDTO.setStartDate(LocalDate.parse("2023-09-04"));
        applyInDTO.setEndDate(LocalDate.parse("2023-09-06"));
        String requestBody = om.writeValueAsString(applyInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/leave/apply").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.id").value(152L));
        resultActions.andExpect(jsonPath("$.data.type").value("ANNUAL"));
        resultActions.andExpect(jsonPath("$.data.usingDays").value(3));
        resultActions.andExpect(jsonPath("$.data.remainDays").value(10));
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
        resultActions.andExpect(jsonPath("$.data.id").value(74L));
        resultActions.andExpect(jsonPath("$.data.type").value("ANNUAL"));
        resultActions.andExpect(jsonPath("$.data.usingDays").value(3));
        resultActions.andExpect(jsonPath("$.data.remainDays").value(10));
        resultActions.andExpect(jsonPath("$.data.status").value("WAITING"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("연차 신청 성공 (공휴일 포함)")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void apply_annual_including_holidays_test() throws Exception {
        // given
        LeaveRequest.ApplyInDTO applyInDTO = new LeaveRequest.ApplyInDTO();
        applyInDTO.setType(LeaveType.valueOf("ANNUAL"));
        applyInDTO.setStartDate(LocalDate.parse("2023-05-26"));
        applyInDTO.setEndDate(LocalDate.parse("2023-05-30"));
        String requestBody = om.writeValueAsString(applyInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/leave/apply").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.id").value(22L));
        resultActions.andExpect(jsonPath("$.data.type").value("ANNUAL"));
        resultActions.andExpect(jsonPath("$.data.usingDays").value(2));
        resultActions.andExpect(jsonPath("$.data.remainDays").value(11));
        resultActions.andExpect(jsonPath("$.data.status").value("WAITING"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("당직 신청 성공")
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
        resultActions.andExpect(jsonPath("$.data.id").value(45L));
        resultActions.andExpect(jsonPath("$.data.type").value("DUTY"));
        resultActions.andExpect(jsonPath("$.data.usingDays").value(0));
        resultActions.andExpect(jsonPath("$.data.remainDays").value(13));
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

    @DisplayName("당직 신청 실패 (중복 신청)")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void apply_duty_fail_duplicate_test() throws Exception {
        // given
        LeaveRequest.ApplyInDTO applyInDTO = new LeaveRequest.ApplyInDTO();
        applyInDTO.setType(LeaveType.valueOf("DUTY"));
        applyInDTO.setStartDate(LocalDate.parse("2023-07-10"));
        applyInDTO.setEndDate(LocalDate.parse("2023-07-10"));
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
        resultActions.andExpect(jsonPath("$.data.value").value("중복된 당직 신청입니다."));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("연차 신청 실패 (이미 신청된 날짜 포함)")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void apply_annual_fail_duplicate_test() throws Exception {
        // given
        LeaveRequest.ApplyInDTO applyInDTO = new LeaveRequest.ApplyInDTO();
        applyInDTO.setType(LeaveType.valueOf("ANNUAL"));
        applyInDTO.setStartDate(LocalDate.parse("2023-07-14"));
        applyInDTO.setEndDate(LocalDate.parse("2023-07-19"));
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
        resultActions.andExpect(jsonPath("$.data.value").value("이미 신청한 연차일이 포함된 신청입니다."));
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
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data.remainDays").value(13));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("당직 신청 취소 성공")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void cancel_duty_test() throws Exception {
        // given
        Long id = 3L;

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/leave/"+id+"/delete"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data.remainDays").value(13));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("연차/당직 신청 취소 실패 (연차/당직 신청 정보가 없을 때)")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void cancel_fail_no_leave_test() throws Exception {
        // given
        Long id = 2000L;

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

    @DisplayName("연차/당직 신청 승인/거절 성공")
    @WithMockUser(username="관리자", roles={"ADMIN"})
    @Test
    public void decide_test() throws Exception {
        // given
        LeaveRequest.DecideInDTO decideInDTO = new LeaveRequest.DecideInDTO();
        decideInDTO.setId(6L);
        decideInDTO.setStatus(LeaveStatus.REJECTION);
        String requestBody = om.writeValueAsString(decideInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/admin/approve").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data.remainDays").value(15));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("연차/당직 신청 승인/거절 실패 (연차/당직 신청 정보가 없을 때)")
    @WithMockUser(username="관리자", roles={"ADMIN"})
    @Test
    public void decide_fail_no_leave_test() throws Exception {
        // given
        LeaveRequest.DecideInDTO decideInDTO = new LeaveRequest.DecideInDTO();
        decideInDTO.setId(5000L);
        decideInDTO.setStatus(LeaveStatus.APPROVAL);
        String requestBody = om.writeValueAsString(decideInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/admin/approve").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(500));
        resultActions.andExpect(jsonPath("$.msg").value("serverError"));
        resultActions.andExpect(jsonPath("$.data").value("해당 연차/당직 신청 정보가 DB에 존재하지 않음"));
        resultActions.andExpect(status().is5xxServerError());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("연차/당직 신청 승인/거절 실패 (이미 탈퇴한 회원의 신청)")
    @WithMockUser(username="관리자", roles={"ADMIN"})
    @Test
    public void decide_fail_status_false_test() throws Exception {
        // given
        LeaveRequest.DecideInDTO decideInDTO = new LeaveRequest.DecideInDTO();
        decideInDTO.setId(5L);
        decideInDTO.setStatus(LeaveStatus.APPROVAL);
        String requestBody = om.writeValueAsString(decideInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/admin/approve").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(500));
        resultActions.andExpect(jsonPath("$.msg").value("serverError"));
        resultActions.andExpect(jsonPath("$.data").value("탈퇴한 회원의 신청입니다."));
        resultActions.andExpect(status().is5xxServerError());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("연차/당직 신청 승인/거절 실패 (이미 승인됨)")
    @WithMockUser(username="관리자", roles={"ADMIN"})
    @Test
    public void decide_fail_already_approved_test() throws Exception {
        // given
        LeaveRequest.DecideInDTO decideInDTO = new LeaveRequest.DecideInDTO();
        decideInDTO.setId(2L);
        decideInDTO.setStatus(LeaveStatus.APPROVAL);
        String requestBody = om.writeValueAsString(decideInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/admin/approve").content(requestBody).contentType(MediaType.APPLICATION_JSON));
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

    @DisplayName("연차/당직 신청 승인/거절 실패 (이미 거절됨)")
    @WithMockUser(username="관리자", roles={"ADMIN"})
    @Test
    public void decide_fail_already_rejected_test() throws Exception {
        // given
        LeaveRequest.DecideInDTO decideInDTO = new LeaveRequest.DecideInDTO();
        decideInDTO.setId(1L);
        decideInDTO.setStatus(LeaveStatus.REJECTION);
        String requestBody = om.writeValueAsString(decideInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/admin/approve").content(requestBody).contentType(MediaType.APPLICATION_JSON));
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

    @DisplayName("연차/당직 신청 승인/거절 실패 (관리자 권한이 아님)")
    @WithMockUser(username="user", roles={"USER"})
    @Test
    public void decide_fail_not_admin_or_master_test() throws Exception {
        // given
        LeaveRequest.DecideInDTO decideInDTO = new LeaveRequest.DecideInDTO();
        decideInDTO.setId(3L);
        decideInDTO.setStatus(LeaveStatus.REJECTION);
        String requestBody = om.writeValueAsString(decideInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/admin/approve").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(403));
        resultActions.andExpect(jsonPath("$.msg").value("forbidden"));
        resultActions.andExpect(jsonPath("$.data").value("권한이 없습니다"));
        resultActions.andExpect(status().isForbidden());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("월별 연차 조회 성공")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_leaves_by_month_success_test() throws Exception {
        // when
        ResultActions resultActions = mvc.perform(get("/auth/leave/month/2023-07"));

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data[0].username").value("김쌀쌀"));
        resultActions.andExpect(jsonPath("$.data[0].type").value("ANNUAL"));
        resultActions.andExpect(jsonPath("$.data[0].status").value("REJECTION"));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("월별 연차 조회 실패 month 형식")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_leaves_by_month_fail_invalid_month_test() throws Exception {
        // when
        ResultActions resultActions = mvc.perform(get("/auth/leave/month/2023-7"));

        // then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("ID로 연차 조회 성공")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_leave_by_id_success_test() throws Exception {
        // given
        Long leaveId = 1L;

        // when
        ResultActions resultActions = mvc.perform(get("/auth/leave/id/{id}", leaveId));

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data[0].username").value("김쌀쌀"));
        resultActions.andExpect(jsonPath("$.data[0].type").value("ANNUAL"));
        resultActions.andExpect(jsonPath("$.data[0].status").value("REJECTION"));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("ID로 연차 조회 실패 (잘못된 ID)")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_leave_by_id_fail_invalid_id_test() throws Exception {
        // given
        Long leaveId = 9999999999L;

        // when
        ResultActions resultActions = mvc.perform(get("/auth/leave/id/{id}", leaveId));

        // then
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("id"));
        resultActions.andExpect(jsonPath("$.data.value").value("아이디를 찾을 수 없습니다"));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("모든 연차/당직 조회 성공")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_all_leaves_success_test() throws Exception {
        // when
        ResultActions resultActions = mvc.perform(get("/auth/leave/all"));

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data[0].username").value("김쌀쌀"));
        resultActions.andExpect(jsonPath("$.data[0].type").value("ANNUAL"));
        resultActions.andExpect(jsonPath("$.data[0].status").value("REJECTION"));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("모든 연차/당직 조회 실패 (인증되지 않은 사용자)")
    @Test
    public void get_all_leaves_fail_unauthenticated_user_test() throws Exception {
        // when
        ResultActions resultActions = mvc.perform(get("/auth/leave/all"));

        // then
        resultActions.andExpect(status().isUnauthorized());
        resultActions.andExpect(jsonPath("$.status").value(401));
        resultActions.andExpect(jsonPath("$.msg").value("unAuthorized"));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }


}

