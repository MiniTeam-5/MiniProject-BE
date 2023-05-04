package shop.mtcoding.restend.model.user;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user_tb")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String username;

    @Column(nullable = false, length = 60) // 패스워드 인코딩(BCrypt)
    private String password;

    @Column(nullable = false, length = 20)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private Boolean status; // true, false

    @Column(nullable = false)
    private LocalDate hireDate;

    private String profile;

    private Integer annualLimit;

    @Min(0)
    private Integer remainDays; // 남은 연차수

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public void changeProfile(String profile) {
        this.profile = profile;
    }

    public void changeEmail(String email) {
        this.email = email;
    }


    public void changePassword(String password) {
        this.password = password;
    }

    public void changeUsername(String username) {
        this.username = username;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void useAnnualLeave(Integer usingDays) {
        this.remainDays -= usingDays;
    }
}