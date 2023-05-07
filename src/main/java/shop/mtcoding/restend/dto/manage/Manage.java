package shop.mtcoding.restend.dto.manage;

import lombok.*;
import shop.mtcoding.restend.model.user.User;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter @Setter
public class Manage {

    private Long userId;
    private Integer remain_days;

    @Builder
    public Manage(Long userId, Integer remain_days) {
        this.userId = userId;
        this.remain_days = remain_days;
    }
    public Manage toEntityOut(User user){
        return Manage.builder()
                .userId(user.getId())
                .remain_days(user.getRemain_days())
                .build();
    }


    @Getter @Setter @NoArgsConstructor
    public static class AnnualRequestDTO{

        // 아무값도 없을경우, default = 0
        @NotNull
        @Min(0)
        @Max(100)
        private Integer remain_days;

        public AnnualRequestDTO(Integer remain_days) {
            this.remain_days = remain_days;
        }
        public User toEntityIn() {
            return User.builder()
                    .remain_days(remain_days)
                    .build();
        }
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
        public UserManageDTO(Long userId, String role, String username, LocalDateTime hire_date, Integer remain_days, String profile) {
                this.userId = userId;
                this.role = role;
                this.username = username;
                this.hire_date = hire_date;
                this.remain_days = remain_days;
                this.profile = profile;
            }

            public User toEntityIn(Long fk){
            return User.builder()
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
                    .userId(user.getId())
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
    public static class MasterInDTO {

        @NotEmpty
        @Pattern(regexp = "ADMIN|USER|MASTER")
        private String role;

        public MasterInDTO(String role) {
            this.role = role;
        }

        public User toEntityIn(Long fk) {
            return User.builder()
                    .id(fk)
                    .role(role)
                    .build();
        }

    }
    @NoArgsConstructor
    @Getter @Setter
    public static class MasterOutDTO{
        @NotNull
        private Long userId;

        @NotEmpty
        private String role;

        @Builder
        public MasterOutDTO(Long userId, String role) {
            this.userId = userId;
            this.role = role;
        }

        public Manage.MasterOutDTO toEntityOut(User user) {
            return MasterOutDTO.builder()
                    .userId (user.getId())
                    .role(user.getRole())
                    .build();
        }
    }




}