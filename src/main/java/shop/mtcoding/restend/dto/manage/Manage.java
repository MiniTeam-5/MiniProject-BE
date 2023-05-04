package shop.mtcoding.restend.dto.manage;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.mtcoding.restend.model.user.User;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter @Setter
public class Manage {


    @NotEmpty
    private Long userId;

    // 아무값도 없을경우, default = 0
    @NotNull
    @Min(0)
    @Max(100)
    private Integer remain_days;

    @Builder
    public Manage(Long userId, Integer remain_days) {
        this.userId = userId;
        this.remain_days = remain_days;
    }
    // checkpoint :url에 id값을 넣지않고 요청하는 경우, toEntityIn(Long id) -> toEntity(user.getId)로 사용해야한다.
    public User toEntityIn(Long fk) {
        return User.builder()
                .id(fk)
                .annual_limit(remain_days)
                .build();
    }

    public Manage toEntityOut(User user) {
        return Manage.builder()
                .userId(user.getId())
                .remain_days(user.getRemain_days())
                .build();
    }

    @NoArgsConstructor
    @Getter @Setter
    public static class UserManageDTO {

        @NotEmpty
        private Long userId;
        @NotEmpty
        private String username;
        @NotEmpty
        @Pattern(regexp = "ADMIN|USER|MASTER")
        private String role;

        @NotEmpty
        private LocalDateTime hire_date;
        @NotNull
        @Min(0)
        @Max(100)
        private Integer remain_days;

        private String profile;
        @Builder
        public UserManageDTO(Long id, String role, String username, LocalDateTime hire_date, Integer remain_days, String profile) {
                this.userId = id;
                this.role = role;
                this.username = username;
                this.hire_date = hire_date;
                this.remain_days = remain_days;
                this.profile = profile;
            }

            public Manage.UserManageDTO toEntityIn(Long fk){
            return UserManageDTO.builder()
                    .id(fk)
                    .role(role)
                    .username(username)
                    .hire_date(hire_date)
                    .remain_days(remain_days)
                    .profile(profile)
                    .build();
        }
        // User -> 회원 관리 객체
        public Manage.UserManageDTO toEntityOut(User user){
            return UserManageDTO.builder()
                    .id(user.getId())
                    .role(user.getRole())
                    .username(user.getUsername())
                    .hire_date(user.getHire_date())
                    .remain_days(user.getRemain_days())
                    .profile(user.getProfile())
                    .build();
        }
    }
    @NoArgsConstructor
    @Getter @Setter
    public static class MasterDTO {

        @NotEmpty
        private Long userId;
        @NotEmpty
        @Pattern(regexp = "ADMIN|USER|MASTER")
        private String role;
        @Builder
        public MasterDTO(Long id, String role) {
            this.userId = id;
            this.role = role;
        }
        public User toEntityIn(Long fk) {
            return User.builder()
                    .id(fk)
                    .role(role)
                    .build();
        }

        public Manage.MasterDTO toEntityOut(User user) {
            return Manage.MasterDTO.builder()
                    .id(user.getId())
                    .role(user.getRole())
                    .build();
        }
    }


}