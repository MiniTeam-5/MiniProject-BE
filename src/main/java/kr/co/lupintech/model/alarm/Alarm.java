package kr.co.lupintech.model.alarm;

import kr.co.lupintech.model.leave.Leave;
import lombok.*;
import kr.co.lupintech.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "alarm_tb")
@Entity
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_id")
    private Leave leave;

    //@Column(nullable = false, length = 60)
    //String content;

    @Column(nullable = false)
    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    @PrePersist//JPA 엔티티가 처음으로 저장되기 직전에 실행되는 콜백 메서드
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
