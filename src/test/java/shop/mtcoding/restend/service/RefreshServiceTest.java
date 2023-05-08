package shop.mtcoding.restend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.test.context.ActiveProfiles;
import shop.mtcoding.restend.core.auth.jwt.MyJwtProvider;
import shop.mtcoding.restend.model.token.RefreshTokenEntity;
import shop.mtcoding.restend.model.token.TokenRepository;
import shop.mtcoding.restend.model.token.TokenStatus;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;
import shop.mtcoding.restend.model.user.UserRole;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static shop.mtcoding.restend.core.auth.jwt.MyJwtProvider.REFRESH_SECRET;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class RefreshServiceTest {

    @InjectMocks
    private RefreshService refreshService;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void 액세스재발급() {

        Long userId = 1L;
        Pair<String, RefreshTokenEntity> refreshInfo = MyJwtProvider.createRefresh();

        String uuid = refreshInfo.getSecond().getUuid();

        User testUser = User.builder().id(userId).username("testUser").role(UserRole.ROLE_USER).build();
        when(tokenRepository.findByUuidAndStatus(uuid, TokenStatus.VALID)).thenReturn(Optional.of(refreshInfo.getSecond()));
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(testUser));

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getHeader(MyJwtProvider.HEADER_REFRESH)).thenReturn(refreshInfo.getFirst());

        String accessjwt = refreshService.액세스재발급(userId, request);

        Assertions.assertThat(accessjwt.startsWith("Bearer ")).isTrue();

    }

    @Test
    void 리프레시토큰회수() {

        Pair<String, RefreshTokenEntity> refreshInfo = MyJwtProvider.createRefresh();

        String uuid = refreshInfo.getSecond().getUuid();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getHeader(MyJwtProvider.HEADER_REFRESH)).thenReturn(refreshInfo.getFirst());
        when(tokenRepository.findByUuidAndStatus(uuid, TokenStatus.VALID)).thenReturn(Optional.of(refreshInfo.getSecond()));

        Assertions.assertThat(true == refreshService.리프레시토큰회수(request));
    }
}