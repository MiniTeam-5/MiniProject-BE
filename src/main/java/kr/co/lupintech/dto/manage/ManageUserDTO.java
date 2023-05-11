package kr.co.lupintech.dto.manage;

import lombok.*;
import kr.co.lupintech.model.user.User;
import kr.co.lupintech.model.user.UserRole;

import javax.validation.constraints.*;
import java.time.LocalDate;

@NoArgsConstructor
@Getter @Setter
public class ManageUserDTO {

    @NotNull
    private Long userId;
    private Integer remainDays;

    @Builder
    public ManageUserDTO(Long userId, Integer remainDays) {
        this.userId = userId;
        this.remainDays = remainDays;
    }
    public ManageUserDTO toEntityOut(User user){
        return ManageUserDTO.builder()
                .userId(user.getId())
                .remainDays(user.getRemainDays())
                .build();
    }


    @Getter @Setter @NoArgsConstructor
    public static class AnnualRequestDTO{

        // 아무값도 없을경우, default = 0
        @NotNull
        @Min(0)
        @Max(100)
        private Integer remainDays;

        public AnnualRequestDTO(Integer remainDays) {
            this.remainDays = remainDays;
        }
        public User toEntityIn() {
            return User.builder()
                    .remainDays(remainDays)
                    .build();
        }
    }


    @NoArgsConstructor
    @Getter @Setter
    public static class ManageUserListDTO {

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
        private Integer remainDays;


        private String profile;
        @Builder
        public ManageUserListDTO(Long userId, UserRole role, String username, LocalDate hireDate, Integer remainDays, String profile) {
                this.userId = userId;
                this.role = role;
                this.username = username;
                this.hireDate = hireDate;
                this.remainDays = remainDays;
                this.profile = profile;
            }

            public User toEntityIn(Long fk){
            return User.builder()
                    .id(fk)
                    .role(role)
                    .username(username)
                    .hireDate(hireDate)
                    .remainDays(remainDays)
                    .profile(profile)
                    .build();
        }
        // User -> 회원 관리 객체
        public ManageUserListDTO toEntityOut(User user){
            return ManageUserListDTO.builder()
                    .userId(user.getId())
                    .role(user.getRole())
                    .username(user.getUsername())
                    .hireDate(user.getHireDate())
                    .remainDays(user.getRemainDays())
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

        public ManageUserDTO.MasterOutDTO toEntityOut(User user) {
            return MasterOutDTO.builder()
                    .userId (user.getId())
                    .role(user.getRole())
                    .build();
        }
    }




}