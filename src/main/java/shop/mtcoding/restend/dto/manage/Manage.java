package shop.mtcoding.restend.dto.manage;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.mtcoding.restend.model.user.User;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter @Setter
public class Manage {

    private Long id;

    private Integer annual_limit;

    @Builder
    public Manage(Long id, Integer annual_limit) {
        this.id = id;
        this.annual_limit = annual_limit;
    }

    public User toEntityIn() {
        return User.builder()
                .id(id)
                .annual_limit(annual_limit)
                .build();
    }

    public Manage toEntityOut(User user) {
        return Manage.builder()
                .id(user.getId())
                .annual_limit(user.getAnnual_limit())
                .build();
    }


        @Getter @Setter
    public static class UserManageDTO {

        private Long id;

        private String username;

        private String role;

        private LocalDateTime hire_date;

        private Integer annual_limit;

        private String profile;
        @Builder
        public UserManageDTO(Long id,String role, String username, LocalDateTime hire_date, Integer annual_limit, String profile) {
                this.id = id;
                this.role = role;
                this.username = username;
                this.hire_date = hire_date;
                this.annual_limit = annual_limit;
                this.profile = profile;
            }

            public Manage.UserManageDTO toEntityIn(){
            return UserManageDTO.builder()
                    .id(id)
                    .role(role)
                    .username(username)
                    .hire_date(hire_date)
                    .annual_limit(annual_limit)
                    .profile(profile)
                    .build();
        }
        // User -> 회원 관리 객체
        public Manage.UserManageDTO toEntityOut(User user){
            return Manage.UserManageDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .hire_date(user.getHire_date())
                    .annual_limit(user.getAnnual_limit())
                    .profile(user.getProfile())
                    .build();
        }
    }
    @Getter @Setter
    public static class MasterDTO {

        private Long id;

        private String role;
        @Builder
        public MasterDTO(Long id, String role) {
            this.id = id;
            this.role = role;
        }
        public User toEntityIn() {
            return User.builder()
                    .id(id)
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