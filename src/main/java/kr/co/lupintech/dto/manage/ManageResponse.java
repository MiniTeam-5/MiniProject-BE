package kr.co.lupintech.dto.manage;

import kr.co.lupintech.model.user.User;
import kr.co.lupintech.model.user.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class ManageResponse {

    @Setter @Getter
    public static class UserOutDTO {
        private Long id; // 유저의 아이디
        private String username;
        private UserRole role;
        private LocalDate hireDate;
        private Integer remainDays;
        private String profile;

        public UserOutDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.role = user.getRole();
            this.hireDate = user.getHireDate();
            this.remainDays = user.getRemainDays();
            this.profile = user.getProfile();
        }
    }
}
