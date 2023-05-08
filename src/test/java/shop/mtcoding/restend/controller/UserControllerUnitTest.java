package shop.mtcoding.restend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import shop.mtcoding.restend.controller.UserController;
import shop.mtcoding.restend.core.advice.MyLogAdvice;
import shop.mtcoding.restend.core.advice.MyValidAdvice;
import shop.mtcoding.restend.core.auth.jwt.MyJwtProvider;
import shop.mtcoding.restend.core.config.MyFilterRegisterConfig;
import shop.mtcoding.restend.core.config.MySecurityConfig;
import shop.mtcoding.restend.core.dummy.DummyEntity;
import shop.mtcoding.restend.dto.user.UserRequest;
import shop.mtcoding.restend.dto.user.UserResponse;
import shop.mtcoding.restend.core.MyWithMockUser;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRole;
import shop.mtcoding.restend.service.RefreshService;
import shop.mtcoding.restend.service.UserService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shop.mtcoding.restend.core.auth.jwt.MyJwtProvider.HEADER;
import static shop.mtcoding.restend.core.auth.jwt.MyJwtProvider.HEADER_REFRESH;

/**
 * @WebMvcTest는 웹 계층 컴포넌트만 테스트로 가져옴
 */

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
        controllers = {UserController.class}
)
public class UserControllerUnitTest extends DummyEntity {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @MockBean
    private UserService userService;

    @MockBean
    private RefreshService refreshService;

    @Test
    public void join_test() throws Exception {
        // 준비
        UserRequest.JoinInDTO joinInDTO = new UserRequest.JoinInDTO();
        joinInDTO.setUsername("코스");
        joinInDTO.setPassword("1234");
        joinInDTO.setEmail("cos@nate.com");
        joinInDTO.setHireDate("2022-12-12");
        String requestBody = om.writeValueAsString(joinInDTO);

        // 가정해볼께


        User cos = newMockUser(1L,"cos", 15);
        UserResponse.JoinOutDTO joinOutDTO = new UserResponse.JoinOutDTO(cos);
        Mockito.when(userService.회원가입(any())).thenReturn(joinOutDTO);

        // 테스트진행
        ResultActions resultActions = mvc
                .perform(post("/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // 검증해볼께
        resultActions.andExpect(jsonPath("$.data.id").value(1L));
        resultActions.andExpect(jsonPath("$.data.username").value("cos"));
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void login_test() throws Exception {
        // given
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setEmail("abcd@nate.com");
        loginInDTO.setPassword("1234");
        String requestBody = om.writeValueAsString(loginInDTO);

        // stub
        Pair<String, String> tokenInfo = Pair.of("Bearer 1234", "Bearer 1234");
        Mockito.when(userService.로그인(any())).thenReturn(tokenInfo);

        UserResponse.LoginOutDTO loginOutDTO = new UserResponse.LoginOutDTO(
                1L, UserRole.ROLE_USER);
        Mockito.when(userService.이메일로회원조회(any())).thenReturn(loginOutDTO);

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
    }


    @MyWithMockUser(id = 1L, username = "cos", role = UserRole.ROLE_USER, remainDays = 15)
    @Test
    public void detail_test() throws Exception {
        // given
        Long id = 1L;

        // stub

        User cos = newMockUser(1L,"cos", 15);

        UserResponse.DetailOutDTO detailOutDTO = new UserResponse.DetailOutDTO(cos);
        Mockito.when(userService.회원상세보기(any())).thenReturn(detailOutDTO);

        // when
        ResultActions resultActions = mvc
                .perform(get("/auth/user"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.username").value("cos"));
        resultActions.andExpect(jsonPath("$.data.email").value("cos@nate.com"));
        resultActions.andExpect(status().isOk());
    }
}

