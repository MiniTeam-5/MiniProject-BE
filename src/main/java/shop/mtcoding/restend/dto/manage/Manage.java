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
    }
}
