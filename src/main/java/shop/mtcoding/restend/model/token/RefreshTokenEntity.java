package shop.mtcoding.restend.model.token;

import lombok.Getter;

import javax.persistence.*;

/*
토큰에 발급한 uuid 를 저장. 그외 필요한 정보들 저장. 리프레시토큰 자체는 저장하지 않는다.
 */
@Getter
@Entity
@Table(name = "token_tb")
public class RefreshTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenStatus status;

    public RefreshTokenEntity() {
    }

    public RefreshTokenEntity(String uuid, TokenStatus status) {
        this.uuid = uuid;
        this.status = status;
    }

    public void setStatus(TokenStatus expired) {
        this.status = expired;
    }
}
