package shop.mtcoding.restend.dto.manage;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.restend.model.user.User;

import java.time.LocalDateTime;

public class Manage {

    private Long id;

    private Integer annual_limit;

    public User toEntity() {
            return User.builder()
                    .id(id)
                    .annual_limit(annual_limit)
                    .build();
        }


        @Getter @Setter
    public static class UserManageDTO {

        private Long id;

        private String username;


        private LocalDateTime hire_date;

        private Integer annual_limit;

        private String profile;

        public User toEntity(){
            return User.builder()
                    .id(id)
                    .username(username)
                    .hire_date(hire_date)
                    .annual_limit(annual_limit)
                    .profile(profile)
                    .build();
        }
        // User -> 회원 관리 객체
        public User toEntityChart(User user){
            return User.builder()
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

        public User toEntity() {
            return User.builder()
                    .id(id)
                    .role(role)
                    .build();
        }
    }


}