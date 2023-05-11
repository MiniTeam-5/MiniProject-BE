package kr.co.lupintech.controller;

import kr.co.lupintech.model.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
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

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void refreshToken() throws Exception {
        // 테스트에 사용할 사용자 저장
        User testUser = userRepository.save(User.builder()
                .username("testUser")
                .email("testUser@nate.com")
                .password("1234")
                .hireDate(LocalDate.now())
                .role(UserRole.ROLE_USER)
                .build());

        // 리프레시 토큰 생성 및 저장
        Pair<String, RefreshTokenEntity> refreshInfo = MyJwtProvider.createRefresh();
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
}