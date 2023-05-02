package shop.mtcoding.restend.model.leave;

import lombok.*;
import shop.mtcoding.restend.model.leave.enumUtil.LeaveStatus;
import shop.mtcoding.restend.model.leave.enumUtil.LeaveType;
import shop.mtcoding.restend.model.user.User;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "leave_tb")
@Entity
public class Leave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private LeaveType type;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveStatus status;
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
}
