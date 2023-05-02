package shop.mtcoding.restend.dto.manage;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.restend.model.user.User;

import java.time.LocalDateTime;

public class Manage {

    @Getter @Setter
    public static class UserManageDTO {

        private String username;

        private String role;

        private LocalDateTime hire_date;

        private Integer annual_limit;

        public User toEntity(){
            return User.builder()
                    .username(username)
                    .role(role)
                    .hire_date(hire_date)
                    .annual_limit(annual_limit)
                    .build();
        }
        // User -> 회원 관리 객체
        public User toEntityChart(User user){
            return User.builder()
                    .username(user.getUsername())
                    .role(user.getRole())
                    .hire_date(user.getHire_date())
                    .annual_limit(user.getAnnual_limit())
                    .build();
        }
    }
}