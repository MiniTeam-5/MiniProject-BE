package kr.co.lupintech.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.lupintech.core.MyRestDoc;
import kr.co.lupintech.core.dummy.DummyEntity;
import kr.co.lupintech.dto.user.UserRequest;
import kr.co.lupintech.model.user.User;
import kr.co.lupintech.model.user.UserRepository;
import kr.co.lupintech.model.user.UserRole;
import kr.co.lupintech.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("관리자 API")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AdminControllerTest extends MyRestDoc {

    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        userRepository.save(User.builder()
                .username("김쌀쌀")
                .password(passwordEncoder.encode("1234"))
                .email("ssar@nate.com")
                .role(UserRole.ROLE_USER)
                .status(true)
                .hireDate(LocalDate.parse("2021-01-21"))
                .remainDays(15)
                .profile("https://lupinbucket.s3.ap-northeast-2.amazonaws.com/person.png")
                .build());
        userRepository.save(User.builder()
                .username("박코스")
                .password(passwordEncoder.encode("1234"))
                .email("cos@nate.com")
                .role(UserRole.ROLE_USER)
                .status(true)
                .hireDate(LocalDate.parse("2022-05-05"))
                .remainDays(15)
                .profile("https://lupinbucket.s3.ap-northeast-2.amazonaws.com/person.png")
                .build());
        userRepository.save(User.builder()
                .username("관리자")
                .password(passwordEncoder.encode("1234"))
                .email("admin" + "@nate.com")
                .role(UserRole.ROLE_ADMIN)
                .status(true)
                .hireDate(LocalDate.parse("2020-03-14"))
                .remainDays(15)
                .profile("https://lupinbucket.s3.ap-northeast-2.amazonaws.com/person.png")
                .build());
        userRepository.save(User.builder()
                .username("마스터")
                .password(passwordEncoder.encode("1234"))
                .email("master"+"@nate.com")
                .role(UserRole.ROLE_MASTER)
                .status(true)
                .hireDate(LocalDate.parse("2020-01-14"))
                .remainDays(15)
                .profile("https://lupinbucket.s3.ap-northeast-2.amazonaws.com/person.png")
                .build());
        userRepository.save(User.builder()
                .username("일년차")
                .password(passwordEncoder.encode("1234"))
                .email("oneyear"+"@nate.com")
                .role(UserRole.ROLE_USER)
                .status(true)
                .hireDate(LocalDate.parse("2022-04-14")) // 입사 1년차라 가정
                .remainDays(0)
                .profile("https://lupinbucket.s3.ap-northeast-2.amazonaws.com/person.png")
                .build());
        userRepository.save(User.builder()
                .username("김신입")
                .password(passwordEncoder.encode("1234"))
                .email("newcomer"+"@nate.com")
                .role(UserRole.ROLE_USER)
                .status(true)
                .hireDate(LocalDate.parse("2023-03-10")) // 입사 2개월이라 가정
                .remainDays(1)
                .profile("https://lupinbucket.s3.ap-northeast-2.amazonaws.com/person.png")
                .build());
        userRepository.save(User.builder()
                .username("김진진")
                .password(passwordEncoder.encode("1234"))
                .email("jin"+"@nate.com")
                .role(UserRole.ROLE_MASTER)
                .status(true)
                .hireDate(LocalDate.parse("2013-05-01")) // 입사 10년차라 가정
                .remainDays(17)
                .profile("https://lupinbucket.s3.ap-northeast-2.amazonaws.com/person.png")
                .build());
        em.clear();
    }

    @DisplayName("회원 목록 조회 (디폴트: 전체 사원 조회, 0번 페이지, 한 페이지에 10개)")
    @WithUserDetails(value = "admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void search_default_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mvc
                .perform(get("/admin"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data.pageNumber").value(0));
        resultActions.andExpect(jsonPath("$.data.pageSize").value(10));
        resultActions.andExpect(jsonPath("$.data.totalElements").value(7));
        resultActions.andExpect(jsonPath("$.data.totalPages").value(1));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("회원 목록 조회 (1번 페이지, 한 페이지에 2개)")
    @WithUserDetails(value = "admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void search_page_size_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mvc
                .perform(get("/admin?page=1&size=2"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data.pageNumber").value(1));
        resultActions.andExpect(jsonPath("$.data.pageSize").value(2));
        resultActions.andExpect(jsonPath("$.data.totalElements").value(7));
        resultActions.andExpect(jsonPath("$.data.totalPages").value(4));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("회원 목록 검색 (전체 이름)")
    @WithUserDetails(value = "admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void search_full_name_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mvc
                .perform(get("/admin?query=김신입"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data.pageNumber").value(0));
        resultActions.andExpect(jsonPath("$.data.pageSize").value(10));
        resultActions.andExpect(jsonPath("$.data.totalElements").value(1));
        resultActions.andExpect(jsonPath("$.data.totalPages").value(1));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("회원 목록 검색 (부분 이름)")
    @WithUserDetails(value = "admin@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void search_partial_name_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mvc
                .perform(get("/admin?query=김"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data.pageNumber").value(0));
        resultActions.andExpect(jsonPath("$.data.pageSize").value(10));
        resultActions.andExpect(jsonPath("$.data.totalElements").value(3));
        resultActions.andExpect(jsonPath("$.data.totalPages").value(1));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
