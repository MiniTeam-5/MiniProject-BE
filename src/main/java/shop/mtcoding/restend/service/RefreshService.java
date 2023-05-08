package shop.mtcoding.restend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.restend.core.auth.jwt.MyJwtProvider;
import shop.mtcoding.restend.core.exception.Exception400;
import shop.mtcoding.restend.core.exception.Exception401;
import shop.mtcoding.restend.model.token.RefreshToken;
import shop.mtcoding.restend.model.token.TokenRepository;
import shop.mtcoding.restend.model.token.TokenStatus;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;

import javax.servlet.http.HttpServletRequest;

import static shop.mtcoding.restend.core.auth.jwt.MyJwtProvider.REFRESH_SECRET;

@Slf4j
@RequiredArgsConstructor
@Service
public class RefreshService {

    private final TokenRepository tokenRepository;

    private final UserRepository userRepository;

    @Transactional
    public String 액세스재발급(Long userId, HttpServletRequest request) {

        String prefixJwt = request.getHeader(MyJwtProvider.HEADER_REFRESH);
        String refreshjwt = prefixJwt.replace(MyJwtProvider.TOKEN_PREFIX, "");

        DecodedJWT decodedJWT = null;

        try {
            decodedJWT = JWT.require(Algorithm.HMAC512(REFRESH_SECRET)).build().verify(refreshjwt);
        } catch (SignatureVerificationException sve) {
            log.error("리프레시 토큰 검증 실패");
            throw new Exception401("리프레시 토큰 검증 실패");
        } catch (TokenExpiredException tee) {
            log.error("리프레시 토큰 만료됨");

            String uuid = decodedJWT.getClaim("uuid").asString();
            RefreshToken RefreshTokenPS = tokenRepository.findByUuid(uuid)
                    .orElseThrow(() -> new Exception401("리프레시 토큰이 존재하지 않습니다."));
            RefreshTokenPS.setStatus(TokenStatus.EXPIRED);
            //tokenRepository.save(RefreshTokenPS);

            throw new Exception401("리프레시 토큰 만료");
        }

        String uuid = decodedJWT.getClaim("uuid").asString();
        // UUID로 리프레시 토큰 조회 및 검증
        tokenRepository.findByUuidAndStatus(uuid, TokenStatus.VALID)
                .orElseThrow(() -> new Exception401("유효한 리프레시 토큰이 존재하지 않습니다."));

        User userPS = userRepository.findById(userId)
                .orElseThrow(() -> new Exception401("해당하는 사용자가 없습니다."));

        // 액세스 토큰 재발급
        String accessjwt = MyJwtProvider.createAccess(userPS);

        return accessjwt;
    }

    //어차피 로그아웃이니까 401은 던지지 말자
    @Transactional
    public void 리프레시토큰회수(HttpServletRequest request) {

        String prefixJwt = request.getHeader(MyJwtProvider.HEADER_REFRESH);
        String refreshjwt = prefixJwt.replace(MyJwtProvider.TOKEN_PREFIX, "");

        DecodedJWT decodedJWT = null;

        try {
            decodedJWT = JWT.require(Algorithm.HMAC512(REFRESH_SECRET)).build().verify(refreshjwt);
        } catch (SignatureVerificationException sve) {
            log.error("리프레시 토큰 검증 실패");
        } catch (TokenExpiredException tee) {
            log.error("리프레시 토큰 만료됨");

            String uuid = decodedJWT.getClaim("uuid").asString();
            RefreshToken RefreshTokenPS = tokenRepository.findByUuid(uuid)
                    .orElseThrow(()->{
                            log.error("리프레시 토큰 존재하지 않음");
                            return new Exception400("refreshtoken", "리프레시 토큰 존재하지 않음");
                    });
            RefreshTokenPS.setStatus(TokenStatus.EXPIRED);

            return;

        }

        String uuid = decodedJWT.getClaim("uuid").asString();
        // UUID로 리프레시 토큰 조회 및 검증
        RefreshToken RefreshTokenPS = tokenRepository.findByUuidAndStatus(uuid, TokenStatus.VALID)
                .orElseThrow(() -> {
                    log.error("유효한 리프레시 토큰 존재하지 않음");
                    return new Exception400("refreshtoken", "유효한 리프레시 토큰이 존재하지 않습니다.");
                });

        RefreshTokenPS.setStatus(TokenStatus.REVOKED);
    }
}
