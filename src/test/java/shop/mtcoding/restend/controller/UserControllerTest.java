package shop.mtcoding.restend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import shop.mtcoding.restend.core.MyRestDoc;
import shop.mtcoding.restend.core.auth.jwt.MyJwtProvider;
import shop.mtcoding.restend.core.dummy.DummyEntity;
import shop.mtcoding.restend.dto.user.UserRequest;
import shop.mtcoding.restend.model.user.UserRepository;
import shop.mtcoding.restend.model.user.UserRole;

import javax.persistence.EntityManager;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("회원 API")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UserControllerTest extends MyRestDoc {

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
        userRepository.save(dummy.newUser("ssar", true, LocalDate.now().minusYears(1).minusWeeks(1), 15));
        userRepository.save(dummy.newUser("cos", true, LocalDate.now().minusYears(1).minusWeeks(1), 15));
        em.clear();
    }

    @DisplayName("회원가입 성공")
    @Test
    public void join_test() throws Exception {
        // given
        UserRequest.JoinInDTO joinInDTO = new UserRequest.JoinInDTO();
        joinInDTO.setUsername("러브");
        joinInDTO.setPassword("1234");
        joinInDTO.setEmail("love@nate.com");
        joinInDTO.setHireDate("2022-12-12");
        String requestBody = om.writeValueAsString(joinInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.id").value(3L));
        resultActions.andExpect(jsonPath("$.data.username").value("러브"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("회원가입 실패")
    @Test
    public void join_fail_bad_request_test() throws Exception {
        // given
        UserRequest.JoinInDTO joinInDTO = new UserRequest.JoinInDTO();
        joinInDTO.setUsername("ssar");
        joinInDTO.setPassword("1234");
        joinInDTO.setEmail("ssar@nate.com");
        joinInDTO.setHireDate("2022-12-12");
        String requestBody = om.writeValueAsString(joinInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("username"));
        resultActions.andExpect(jsonPath("$.data.value").value("이름은 2~20자 이내로 작성해주세요"));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("회원가입 유효성 검사 실패")
    @Test
    public void join_fail_valid_test() throws Exception {
        // given
        UserRequest.JoinInDTO joinInDTO = new UserRequest.JoinInDTO();
        joinInDTO.setUsername("s");
        joinInDTO.setPassword("1234");
        joinInDTO.setEmail("ssar@nate.com");
        joinInDTO.setHireDate("2023-05-05");//이게 없으면 Username, HireDate 에서 번갈아 가면서 오류가 발생합니다. JoinInDTO 객체를 참고하세요.
        String requestBody = om.writeValueAsString(joinInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("username"));
        resultActions.andExpect(jsonPath("$.data.value").value("이름은 2~20자 이내로 작성해주세요"));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("로그인 성공")
    @Test
    public void login_test() throws Exception {
        // given
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setEmail("ssar@nate.com");
        loginInDTO.setPassword("1234");
        String requestBody = om.writeValueAsString(loginInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/login").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        String jwtToken = resultActions.andReturn().getResponse().getHeader(MyJwtProvider.HEADER);
        Assertions.assertThat(jwtToken.startsWith(MyJwtProvider.TOKEN_PREFIX)).isTrue();
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("로그인 인증 실패")
    @Test
    public void login_fail_un_authorized_test() throws Exception {
        // given
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setEmail("abc@nate.com");
        loginInDTO.setPassword("12345");
        String requestBody = om.writeValueAsString(loginInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/login").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(401));
        resultActions.andExpect(jsonPath("$.msg").value("unAuthorized"));
        resultActions.andExpect(jsonPath("$.data").value("인증되지 않았습니다"));
        resultActions.andExpect(status().isUnauthorized());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    // jwt token -> 인증필터 -> 시큐리티 세션생성
    // setupBefore=TEST_METHOD (setUp 메서드 실행전에 수행)
    // setupBefore=TEST_EXECUTION (saveAccount_test 메서드 실행전에 수행)
    // @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    // authenticationManager.authenticate() 실행해서 MyUserDetailsService를 호출하고
    // usrename=ssar을 찾아서 세션에 담아주는 어노테이션
    @DisplayName("회원상세보기 성공")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void detail_test() throws Exception {
        // given
        Long id = 1L;

        // when
        ResultActions resultActions = mvc
                .perform(get("/auth/user"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
//        resultActions.andExpect(jsonPath("$.data.id").value(1L));
        resultActions.andExpect(jsonPath("$.data.username").value("ssar"));
        resultActions.andExpect(jsonPath("$.data.email").value("ssar@nate.com"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("회원상세보기 인증 실패")
    @Test
    public void detail_fail_un_authorized__test() throws Exception {
        // given
        Long id = 1L;

        // when
        ResultActions resultActions = mvc
                .perform(get("/auth/user"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(401));
        resultActions.andExpect(jsonPath("$.msg").value("unAuthorized"));
        resultActions.andExpect(jsonPath("$.data").value("인증되지 않았습니다"));
        resultActions.andExpect(status().isUnauthorized());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

//    @DisplayName("회원상세보기 권한 실패")
//    @WithUserDetails(value = "cos@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    public void detail_fail_forbidden_test() throws Exception {
//        // given
//        Long id = 1L;
//
//        // when
//        ResultActions resultActions = mvc
//                .perform(get("/auth/user"));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트 : " + responseBody);
//
//        // then
//        resultActions.andExpect(jsonPath("$.status").value(403));
//        resultActions.andExpect(jsonPath("$.msg").value("forbidden"));
//        resultActions.andExpect(jsonPath("$.data").value("권한이 없습니다"));
//        resultActions.andExpect(status().isForbidden());
//        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
//    }

    @DisplayName("프로필, 사원명, 이메일, 비밀번호 변경")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void modify_all_test() throws Exception {

        //Given
        UserRequest.ModifiedInDTO modifiedInDTO = new UserRequest.ModifiedInDTO();
        modifiedInDTO.setEmail("asdf@nate.com");
        modifiedInDTO.setUsername("김이박");
        modifiedInDTO.setNewPassword("1234");

        //when
        MockMultipartFile profile = new MockMultipartFile(
                "profile", "person.png", "image/png", new FileInputStream("./upload/person.png"));

        // modifiedInDTO 객체를 JSON 문자열로 변환
        String modifiedInJson = om.writeValueAsString(modifiedInDTO);
        MockMultipartFile json = new MockMultipartFile("modifiedInDTO", "modifiedInDTO", "application/json", modifiedInJson.getBytes(StandardCharsets.UTF_8));

        //then
        mvc.perform(
                        multipart("/auth/user")
                                .file(profile)
                                .file(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("asdf@nate.com"))
                .andExpect(jsonPath("$.data.username").value("김이박"))
                .andExpect(jsonPath("$.data.passwordReset").value(true))
                .andExpect(jsonPath("$.data.profileReset").value(true))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("사원명, 이메일, 비밀번호 변경")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void modify_name_email_password_test() throws Exception {

        //Given
        UserRequest.ModifiedInDTO modifiedInDTO = new UserRequest.ModifiedInDTO();
        modifiedInDTO.setEmail("asdf@nate.com");
        modifiedInDTO.setUsername("테스터");
        modifiedInDTO.setNewPassword("1234");

        //when
        MockMultipartFile profile = new MockMultipartFile("profile", "".getBytes());

        // modifiedInDTO 객체를 JSON 문자열로 변환
        String modifiedInJson = om.writeValueAsString(modifiedInDTO);
        MockMultipartFile json = new MockMultipartFile("modifiedInDTO", "modifiedInDTO", "application/json", modifiedInJson.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                        multipart("/auth/user")
                                .file(profile)
                                .file(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("asdf@nate.com"))
                .andExpect(jsonPath("$.data.username").value("테스터"))
                .andExpect(jsonPath("$.data.passwordReset").value(true))
                .andExpect(jsonPath("$.data.profileReset").value(false))
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("auth/user", requestParts(
                        partWithName("profile").description("The file to upload"),
                        partWithName("modifiedInDTO").description("modifiedInDTO"))
                ));
    }

    @DisplayName("사원명, 이메일만 변경")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void modify_username_email() throws Exception {

        //Given
        UserRequest.ModifiedInDTO modifiedInDTO = new UserRequest.ModifiedInDTO();
        modifiedInDTO.setEmail("asdf@nate.com");
        modifiedInDTO.setUsername("키키");

        //when
        MockMultipartFile profile = new MockMultipartFile("profile", "".getBytes());

        // modifiedInDTO 객체를 JSON 문자열로 변환
        String modifiedInJson = om.writeValueAsString(modifiedInDTO);
        MockMultipartFile json = new MockMultipartFile("modifiedInDTO", "modifiedInDTO", "application/json", modifiedInJson.getBytes(StandardCharsets.UTF_8));

        // when
        ResultActions resultActions = mvc
                .perform(
                        multipart("/auth/user")
                                .file(profile)
                                .file(json));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data.email").value("asdf@nate.com"));
        resultActions.andExpect(jsonPath("$.data.username").value("키키"));
        resultActions.andExpect(jsonPath("$.data.passwordReset").value(false));
        resultActions.andExpect(jsonPath("$.data.profileReset").value(false));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

//    @DisplayName("비밀번호 변경 실패")
//    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    public void modify_password_fail() throws Exception {
//
//        //Given
//        UserRequest.ModifiedInDTO modifiedInDTO = new UserRequest.ModifiedInDTO();
//        modifiedInDTO.setEmail("asdf@nate.com");
//        modifiedInDTO.setUsername("asdf");
//        modifiedInDTO.setNewPassword("1234");
//
//        //when
//        MockMultipartFile profile = new MockMultipartFile("profile", "".getBytes());
//
//        // modifiedInDTO 객체를 JSON 문자열로 변환
//        String modifiedInJson = om.writeValueAsString(modifiedInDTO);
//        MockMultipartFile json = new MockMultipartFile("modifiedInDTO", "modifiedInDTO", "application/json", modifiedInJson.getBytes(StandardCharsets.UTF_8));
//
//        // when
//        ResultActions resultActions = mvc
//                .perform(
//                        multipart("/auth/user/1")
//                                .file(profile)
//                                .file(json));
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트 : " + responseBody);
//
//        //then
//        resultActions.andExpect(jsonPath("$.status").value(400));
//        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
//        resultActions.andExpect(jsonPath("$.data.value").value("비밀번호 재확인 필요"));
//        resultActions.andExpect(status().isBadRequest());
//        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
//    }
}

