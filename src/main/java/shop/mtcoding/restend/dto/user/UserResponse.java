package shop.mtcoding.restend.dto.user;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRole;

public class UserResponse {

    @Getter @Setter
    public static class DetailOutDTO{
        private Long id;
        private String email;
        private String username;
        private String profile;
        private UserRole role;

        public DetailOutDTO(User user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.username = user.getUsername();
            this.profile = user.getProfile();
            this.role = user.getRole();
        }
    }

    @Setter
    @Getter
    public static class JoinOutDTO {
        private Long id;
        private String username;

        public JoinOutDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
        }
    }

    @Setter
    @Getter
    public static class LoginOutDTO {
        private Long id;
        private UserRole role;

        public LoginOutDTO(Long id, UserRole role) {
            this.id = id;
            this.role = role;
        }
    }

    @Getter @Setter
    public static class ModifiedOutDTO {

        private String email;
        private String username;
        private Boolean passwordReset;
        private Boolean profileReset;

        public ModifiedOutDTO(User user) {
            this.email = user.getEmail();
            this.username = user.getUsername();
            this.passwordReset = false;
            this.profileReset = false;
        }
    }
}
