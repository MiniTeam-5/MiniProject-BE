package shop.mtcoding.restend.dto.manage;

import lombok.*;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRole;

import javax.validation.constraints.*;
import java.time.LocalDate;
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
                .remain_days(user.getRemainDays())
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
                    .remainDays(remain_days)
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
        private UserRole role;

        @NotEmpty
        private LocalDate hireDate;
        @NotNull
        @Min(0)
        @Max(100)
        private Integer remain_days;

        private String profile;
        @Builder
        public UserManageDTO(Long userId, UserRole role, String username, LocalDate hireDate, Integer remain_days, String profile) {
                this.userId = userId;
                this.role = role;
                this.username = username;
                this.hireDate = hireDate;
                this.remain_days = remain_days;
                this.profile = profile;
            }

            public User toEntityIn(Long fk){
            return User.builder()
                    .id(fk)
                    .role(role)
                    .username(username)
                    .hireDate(hireDate)
                    .remainDays(remain_days)
                    .profile(profile)
                    .build();
        }
        // User -> 회원 관리 객체
        public Manage.UserManageDTO toEntityOut(User user){
            return UserManageDTO.builder()
                    .userId(user.getId())
                    .role(user.getRole())
                    .username(user.getUsername())
                    .hireDate(user.getHireDate())
                    .remain_days(user.getRemainDays())
                    .profile(user.getProfile())
                    .build();
        }
    }
    @NoArgsConstructor
    @Getter @Setter
    public static class MasterInDTO {

        @NotEmpty
        private UserRole role;

        public MasterInDTO(UserRole role) {
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
        private UserRole role;

        @Builder
        public MasterOutDTO(Long userId, UserRole role) {
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