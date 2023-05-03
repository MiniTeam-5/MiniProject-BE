package shop.mtcoding.restend.dto.manage;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.mtcoding.restend.model.user.User;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter @Setter
public class Manage {

    private Long id;

    private Integer annual_count;

    @Builder
    public Manage(Long id, Integer annual_count) {
        this.id = id;
        this.annual_count = annual_count;
    }

    public User toEntityIn() {
        return User.builder()
                .id(id)
                .annual_limit(annual_count)
                .build();
    }

    public Manage toEntityOut(User user) {
        return Manage.builder()
                .id(user.getId())
                .annual_count(user.getAnnual_count())
                .build();
    }

    @NoArgsConstructor
    @Getter @Setter
    public static class UserManageDTO {

        private Long id;

        private String username;
        private String role;

        private LocalDateTime hire_date;

        private Integer annual_count;

        private String profile;
        @Builder
        public UserManageDTO(Long id,String role, String username, LocalDateTime hire_date, Integer annual_count, String profile) {
                this.id = id;
                this.role = role;
                this.username = username;
                this.hire_date = hire_date;
                this.annual_count = annual_count;
                this.profile = profile;
            }

            public Manage.UserManageDTO toEntityIn(){
            return UserManageDTO.builder()
                    .id(id)
                    .role(role)
                    .username(username)
                    .hire_date(hire_date)
                    .annual_count(annual_count)
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
                    .annual_count(user.getAnnual_count())
                    .profile(user.getProfile())
                    .build();
        }
    }
    @NoArgsConstructor
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