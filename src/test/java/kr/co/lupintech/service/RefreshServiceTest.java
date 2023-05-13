package kr.co.lupintech.service;

import kr.co.lupintech.model.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.test.context.ActiveProfiles;
import kr.co.lupintech.core.auth.jwt.MyJwtProvider;
import kr.co.lupintech.model.token.RefreshTokenEntity;
import kr.co.lupintech.model.token.TokenRepository;
import kr.co.lupintech.model.token.TokenStatus;
import kr.co.lupintech.model.user.User;
import kr.co.lupintech.model.user.UserRole;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.mockito.Mockito.when;

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

        User testUser = User.builder().id(userId).username("testUser").role(UserRole.ROLE_USER).build();
        Pair<String, RefreshTokenEntity> refreshInfo = MyJwtProvider.createRefresh(testUser);
        String uuid = refreshInfo.getSecond().getUuid();

        when(tokenRepository.findByUuidAndStatus(uuid, TokenStatus.VALID)).thenReturn(Optional.of(refreshInfo.getSecond()));
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(testUser));

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getHeader(MyJwtProvider.HEADER_REFRESH)).thenReturn(refreshInfo.getFirst());

        String accessjwt = refreshService.액세스재발급(userId, request);

        Assertions.assertThat(accessjwt.startsWith("Bearer ")).isTrue();

    }

    @Test
    void 리프레시토큰회수() {

        Long userId = 1L;

        User testUser = User.builder().id(userId).username("testUser").role(UserRole.ROLE_USER).build();
        Pair<String, RefreshTokenEntity> refreshInfo = MyJwtProvider.createRefresh(testUser);
        String uuid = refreshInfo.getSecond().getUuid();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getHeader(MyJwtProvider.HEADER_REFRESH)).thenReturn(refreshInfo.getFirst());
        when(tokenRepository.findByUuidAndStatus(uuid, TokenStatus.VALID)).thenReturn(Optional.of(refreshInfo.getSecond()));

        Assertions.assertThat(true == refreshService.리프레시토큰회수(request));
    }
}