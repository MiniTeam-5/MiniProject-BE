package kr.co.lupintech.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.lupintech.core.MyRestDoc;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import kr.co.lupintech.core.auth.jwt.MyJwtProvider;
import kr.co.lupintech.core.dummy.DummyEntity;

import javax.persistence.EntityManager;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static kr.co.lupintech.core.auth.jwt.MyJwtProvider.HEADER;
import static kr.co.lupintech.core.auth.jwt.MyJwtProvider.HEADER_REFRESH;

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
    @Autowired
    private S3Service s3Service;

    @BeforeEach
    public void setUp() {
        userRepository.save(dummy.newUser("ssar", true, LocalDate.now().minusYears(1).minusWeeks(1), 15));
        userRepository.save(dummy.newUser("cos", true, LocalDate.now().minusYears(1).minusWeeks(1), 15));
        userRepository.save(dummy.newUser("resign", true, LocalDate.now().minusYears(1).minusWeeks(1), 15));
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        userRepository.save(
                User.builder()
                        .username("update")
                        .password(passwordEncoder.encode("1234"))
                        .email("update@nate.com")
                        .role(UserRole.ROLE_USER)
                        .profile("https://lupinbucket.s3.ap-northeast-2.amazonaws.com/person.png")
                        .hireDate(LocalDate.parse("2021-09-04"))
                        .remainDays(13)
                        .build());
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

        //resultActions.andExpect(jsonPath("$.data.id").value(3L)); //3을 보장할 수 없습니다.
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

        MvcResult mvcResult = resultActions.andReturn();
        String accessToken = mvcResult.getResponse().getHeader(HEADER);
        String refreshToken = mvcResult.getResponse().getHeader(HEADER_REFRESH);

        // then
        assertThat(accessToken).startsWith(MyJwtProvider.TOKEN_PREFIX);
        assertThat(refreshToken).startsWith(MyJwtProvider.TOKEN_PREFIX);
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
    @DisplayName("개인정보 가져오기 성공")
    @WithUserDetails(value = "update@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
        resultActions.andExpect(jsonPath("$.data.id").value(4L));
        resultActions.andExpect(jsonPath("$.data.username").value("update"));
        resultActions.andExpect(jsonPath("$.data.email").value("update@nate.com"));
        resultActions.andExpect(jsonPath("$.data.role").value("ROLE_USER"));
        resultActions.andExpect(jsonPath("$.data.profile").value("https://lupinbucket.s3.ap-northeast-2.amazonaws.com/person.png"));
        resultActions.andExpect(jsonPath("$.data.remainDays").value(13));
        resultActions.andExpect(jsonPath("$.data.hireDate").value("2021-09-04"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("개인정보 가져오기  인증 실패")
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
    public void modify_every_test() throws Exception {

        //Given
        UserRequest.ModifiedInDTO modifiedInDTO = new UserRequest.ModifiedInDTO();
        modifiedInDTO.setEmail("asdf@nate.com");
        modifiedInDTO.setUsername("김이박");
        modifiedInDTO.setNewPassword("1234");

        MockMultipartFile profile = new MockMultipartFile(
                "profile", "person.png", "image/png"
                , new FileInputStream("./src/main/resources/dummy/person.png"));

        // modifiedInDTO 객체를 JSON 문자열로 변환
        String modifiedInJson = om.writeValueAsString(modifiedInDTO);
        MockMultipartFile json = new MockMultipartFile("modifiedInDTO", "modifiedInDTO", "application/json", modifiedInJson.getBytes(StandardCharsets.UTF_8));

        //when
        ResultActions resultActions = mvc.perform(
                multipart("/auth/user")
                        .file(profile)
                        .file(json));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.email").value("asdf@nate.com"));
        resultActions.andExpect(jsonPath("$.data.username").value("김이박"));
        resultActions.andExpect(jsonPath("$.data.passwordReset").value(true));
        resultActions.andExpect(jsonPath("$.data.profileReset").value(true));
        resultActions.andExpect(jsonPath("$.data.profile").exists());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
//        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document("auth/user", requestParts(
//                partWithName("profile").description("The file to upload"),
//                partWithName("modifiedInDTO").description("modifiedInDTO"))
//        ));

        // S3에 저장안되게 다시 삭제시켜주기
        int start = responseBody.indexOf("\"profile\":\"") + "\"profile\":\"".length();
        int end = responseBody.indexOf("\"}}", start);

        String profileUrl = responseBody.substring(start, end);
        s3Service.delete(profileUrl);
    }

    @DisplayName("프로필 삭제, 사원명, 이메일, 비밀번호 변경")
    @WithUserDetails(value = "update@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void modify_all_delete_profile_test() throws Exception {

        //Given
        MockMultipartFile init = new MockMultipartFile(
                "init", "person.png", "image/png"
                , new FileInputStream("./src/main/resources/dummy/person.png"));
        String path = s3Service.upload(init);
        UserRequest.ModifiedInDTO modifiedInDTO = new UserRequest.ModifiedInDTO();
        modifiedInDTO.setEmail("asdf@nate.com");
        modifiedInDTO.setUsername("김이박");
        modifiedInDTO.setNewPassword("1234");
        modifiedInDTO.setProfileToDelete(path);

        //when
        MockMultipartFile profile = new MockMultipartFile("profile", "".getBytes());

        // modifiedInDTO 객체를 JSON 문자열로 변환
        String modifiedInJson = om.writeValueAsString(modifiedInDTO);
        MockMultipartFile json = new MockMultipartFile("modifiedInDTO", "modifiedInDTO", "application/json", modifiedInJson.getBytes(StandardCharsets.UTF_8));

        //then
        ResultActions resultActions = mvc.perform(
                multipart("/auth/user")
                        .file(profile)
                        .file(json));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.email").value("asdf@nate.com"));
        resultActions.andExpect(jsonPath("$.data.username").value("김이박"));
        resultActions.andExpect(jsonPath("$.data.passwordReset").value(true));
        resultActions.andExpect(jsonPath("$.data.profileReset").value(true));
        resultActions.andExpect(jsonPath("$.data.profile").value("https://lupinbucket.s3.ap-northeast-2.amazonaws.com/person.png"));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("사원명, 이메일, 비밀번호 변경")
    @WithUserDetails(value = "update@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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

        ResultActions resultActions = mvc.perform(
                multipart("/auth/user")
                        .file(profile)
                        .file(json));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.email").value("asdf@nate.com"));
        resultActions.andExpect(jsonPath("$.data.username").value("테스터"));
        resultActions.andExpect(jsonPath("$.data.passwordReset").value(true));
        resultActions.andExpect(jsonPath("$.data.profileReset").value(false));
        resultActions.andExpect(jsonPath("$.data.profile").value("https://lupinbucket.s3.ap-northeast-2.amazonaws.com/person.png"));
        resultActions.andDo(MockMvcResultHandlers.print());
        resultActions.andDo(document);
    }

    @DisplayName("사원명, 이메일만 변경")
    @WithUserDetails(value = "update@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
        resultActions.andExpect(jsonPath("$.data.profile").value("https://lupinbucket.s3.ap-northeast-2.amazonaws.com/person.png"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("개인 정보 수정 유효성 검사 실패 (사원명이 없는 경우)")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void modify_valid_test_fail_no_username() throws Exception {

        //Given
        UserRequest.ModifiedInDTO modifiedInDTO = new UserRequest.ModifiedInDTO();
        modifiedInDTO.setEmail("asdf@nate.com");
        modifiedInDTO.setNewPassword("1234");

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
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("username"));
        resultActions.andExpect(jsonPath("$.data.value").value("must not be empty"));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("개인 정보 수정 유효성 검사 실패 (이메일이 없는 경우)")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void modify_valid_test_fail_no_email() throws Exception {
        //Given
        UserRequest.ModifiedInDTO modifiedInDTO = new UserRequest.ModifiedInDTO();
        modifiedInDTO.setUsername("테스터");
        modifiedInDTO.setNewPassword("1234");

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
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("email"));
        resultActions.andExpect(jsonPath("$.data.value").value("must not be empty"));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("개인 정보 수정 유효성 검사 실패 (이메일 형식 오류)")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void modify_valid_test_fail_email_format() throws Exception {
        //Given
        UserRequest.ModifiedInDTO modifiedInDTO = new UserRequest.ModifiedInDTO();
        modifiedInDTO.setUsername("테스터");
        modifiedInDTO.setEmail("asdfnate.com");
        modifiedInDTO.setNewPassword("1234");

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
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("email"));
        resultActions.andExpect(jsonPath("$.data.value").value("이메일 형식으로 작성해주세요"));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("개인 정보 수정 실패 (수정 사항이 없는 경우)")
    @WithUserDetails(value = "ssar@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void modify_fail_nothing_to_modify_test() throws Exception {

        //Given
        UserRequest.ModifiedInDTO modifiedInDTO = new UserRequest.ModifiedInDTO();
        modifiedInDTO.setEmail("ssar@nate.com");
        modifiedInDTO.setUsername("ssar");

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
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("profile, profileToDelete, email, username, newPassword"));
        resultActions.andExpect(jsonPath("$.data.value").value("수정사항이 없습니다."));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("유저 삭제(비활성) 성공")
    @WithMockUser(username="admin", roles={"ADMIN"})
    @Test
    public void resign_test() throws Exception {
        // given
        Long id = 3L;

        // when
        ResultActions resultActions = mvc
                .perform(post("/admin/resign/"+id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("유저 삭제(비활성) 실패 (없는 유저)")
    @WithMockUser(username="admin", roles={"ADMIN"})
    @Test
    public void resign_fail_test() throws Exception {
        // given
        Long id = 5L;

        // when
        ResultActions resultActions = mvc
                .perform(post("/admin/resign/"+id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(500));
        resultActions.andExpect(jsonPath("$.msg").value("serverError"));
        resultActions.andExpect(jsonPath("$.data").value("로그인 된 유저가 DB에 존재하지 않음"));
        resultActions.andExpect(status().is5xxServerError());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}

