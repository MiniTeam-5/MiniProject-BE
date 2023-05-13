package kr.co.lupintech.controller;

import kr.co.lupintech.core.auth.jwt.MyJwtProviderTest;
import kr.co.lupintech.core.exception.Exception401;
import kr.co.lupintech.model.token.TokenStatus;
import kr.co.lupintech.model.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import kr.co.lupintech.core.MyRestDoc;
import kr.co.lupintech.core.auth.jwt.MyJwtProvider;
import kr.co.lupintech.model.token.RefreshTokenEntity;
import kr.co.lupintech.model.token.TokenRepository;
import kr.co.lupintech.model.user.User;
import kr.co.lupintech.model.user.UserRole;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("리프레시 API")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class RefreshControllerTest extends MyRestDoc {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @DisplayName("리프레쉬토큰 성공")
    @Test
    void refreshToken() throws Exception {
        // 테스트에 사용할 사용자 저장
        User testUser = userRepository.save(User.builder()
                .username("테스트")
                .email("testUser@nate.com")
                .password("1234")
                .hireDate(LocalDate.now())
                .role(UserRole.ROLE_USER)
                .build());

        // 리프레시 토큰 생성 및 저장
        Pair<String, RefreshTokenEntity> refreshInfo = MyJwtProvider.createRefresh(testUser);
        tokenRepository.save(refreshInfo.getSecond());

        // 헤더에 리프레시 토큰 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add(MyJwtProvider.HEADER_REFRESH, refreshInfo.getFirst());

        // refreshToken API 호출
        ResultActions resultActions = mockMvc.perform(post("/refreshtoken/{id}",
                        testUser.getId()).headers(headers).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists(MyJwtProvider.HEADER));


        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("리프레쉬토큰 실패 (만료 토큰)")
    @Test
    void refreshToken_fail_token_expired() throws Exception {
        // 테스트에 사용할 사용자 저장
        User testUser = userRepository.save(User.builder()
                .username("testUser")
                .email("testUser@nate.com")
                .password("1234")
                .hireDate(LocalDate.now())
                .role(UserRole.ROLE_USER)
                .build());

        Pair<String, RefreshTokenEntity> randomTokenPair = MyJwtProviderTest.testCreateExpiredRefresh();

            // 헤더에 가짜 토큰 추가
            HttpHeaders headers = new HttpHeaders();
            headers.add(MyJwtProvider.HEADER_REFRESH, randomTokenPair.getFirst());

            // refreshToken API 호출
            ResultActions resultActions = mockMvc.perform(post("/refreshtoken/{id}", testUser.getId())
                            .headers(headers)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof Exception401))
                    .andExpect(result -> assertEquals("리프레시 토큰 만료", result.getResolvedException().getMessage()));

            resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

}