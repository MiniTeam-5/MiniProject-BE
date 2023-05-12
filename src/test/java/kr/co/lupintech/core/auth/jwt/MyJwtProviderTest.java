package kr.co.lupintech.core.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import kr.co.lupintech.model.token.RefreshTokenEntity;
import kr.co.lupintech.model.token.TokenStatus;
import kr.co.lupintech.model.user.User;
import org.junit.jupiter.api.Test;

import org.springframework.data.util.Pair;


import java.util.Date;
import java.util.UUID;


public class MyJwtProviderTest extends MyJwtProvider{

    @Test
    public static String testcreateAccess(User user) {
        String jwt = JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP_ACCESS))
                .withClaim("id", user.getId())
                .withClaim("role", user.getRole().name())
                .sign(Algorithm.HMAC512(ACCESS_SECRET));
        return TOKEN_PREFIX + jwt;
    }

    @Test
    public static Pair<String, RefreshTokenEntity> testCreateRefresh() {

        String uuid = UUID.randomUUID().toString();

        RefreshTokenEntity refreshToken = new RefreshTokenEntity(uuid, TokenStatus.VALID);

        String jwt = JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP_REFRESH))
                .withClaim("uuid", uuid)
                .sign(Algorithm.HMAC512(REFRESH_SECRET));
        return Pair.of(TOKEN_PREFIX + jwt, refreshToken);
    }
    @Test
    public static Pair<String, RefreshTokenEntity> testCreateExpiredRefresh() {

        String uuid = UUID.randomUUID().toString();

        RefreshTokenEntity refreshToken = new RefreshTokenEntity(uuid, TokenStatus.VALID);

        String jwt = JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() - 1000))
                .withClaim("uuid", uuid)
                .sign(Algorithm.HMAC512(REFRESH_SECRET));
        return Pair.of(TOKEN_PREFIX + jwt, refreshToken);
    }
}
