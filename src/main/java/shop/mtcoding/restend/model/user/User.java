package shop.mtcoding.restend.model.user;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


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
    @Column(nullable = false, length = 20)
    private String fullName;
    @Column(nullable = false)
    private String role;
    @Column(nullable = false)
    private Boolean status; // true, false

//    @Column(nullable = false)
//    private Integer annual_limit;

    @Column(nullable = false)
    private Integer annual_count;

    @Column(nullable = false)
    private LocalDateTime hire_date;

    private String profile;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public User(Long id, String username, String password, String email, String fullName, String role, Boolean status, Integer annual_limit, Integer annual_count, LocalDateTime hire_date, String profile, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.status = status;
        //this.annual_limit = annual_limit;
        this.annual_count = annual_count;
        this.hire_date = hire_date;
        this.profile = profile;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void update(User user){
        this.username = user.getUsername();
        this.role = user.getRole();
        this.hire_date = user.getHire_date();
        //this.annual_limit = user.getAnnual_limit();
    }
}