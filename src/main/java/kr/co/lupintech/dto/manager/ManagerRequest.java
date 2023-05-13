package kr.co.lupintech.dto.manager;

import lombok.*;
import kr.co.lupintech.model.user.User;
import kr.co.lupintech.model.user.UserRole;

import javax.validation.constraints.*;
import java.time.LocalDate;

public class ManagerRequest {

    @Getter @Setter
    public static class AnnualInDTO{

        @NotNull
        @Min(0)
        @Max(25)
        private Integer remainDays;

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

    @Getter @Setter
    public static class MasterInDTO {

        @NotEmpty
        private UserRole role;

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

        public ManagerRequest.MasterOutDTO toEntityOut(User user) {
            return MasterOutDTO.builder()
                    .userId (user.getId())
                    .role(user.getRole())
                    .build();
        }
    }




}