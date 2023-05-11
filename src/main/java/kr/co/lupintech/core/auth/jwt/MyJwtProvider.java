package kr.co.lupintech.core.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import kr.co.lupintech.model.token.RefreshTokenEntity;
import kr.co.lupintech.model.token.TokenStatus;
import kr.co.lupintech.model.user.User;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class MyJwtProvider {

    private static final String SUBJECT = "jwtstudy";
    private static final int EXP_ACCESS = 1000 * 60 * 60* 24; // 24시간
    private static final int EXP_REFRESH = 1000 * 60 * 60* 24 * 7; // 7일
    public static final String TOKEN_PREFIX = "Bearer "; // 스페이스 필요함
    public static final String HEADER = "Authorization";

    public static final String HEADER_REFRESH = "RefreshToken";
    private static final String ACCESS_SECRET = "lupintech";
    public static final String REFRESH_SECRET = "miniproject";

    //private static final String SECRET = System.getenv("HS512_SECRET");

    public static String createAccess(User user) {
        String jwt = JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP_ACCESS))
                .withClaim("id", user.getId())
                .withClaim("role", user.getRole().name())   //string -> enum 으로 바뀌면서 .name()클래스 추가
                .sign(Algorithm.HMAC512(ACCESS_SECRET));
        return TOKEN_PREFIX + jwt;
    }

    public static Pair<String, RefreshTokenEntity> createRefresh() {

        String uuid = UUID.randomUUID().toString();

        RefreshTokenEntity refreshToken = new RefreshTokenEntity(uuid, TokenStatus.VALID);

        String jwt = JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP_REFRESH))
                .withClaim("uuid", uuid)
                .sign(Algorithm.HMAC512(REFRESH_SECRET));
        return Pair.of(TOKEN_PREFIX + jwt, refreshToken);
    }

    public static DecodedJWT verify(String jwt) throws SignatureVerificationException, TokenExpiredException {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(ACCESS_SECRET))
                .build().verify(jwt);
        return decodedJWT;
    }
}