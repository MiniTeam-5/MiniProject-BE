package kr.co.lupintech.dto.user;

import kr.co.lupintech.model.user.UserRole;
import lombok.Getter;
import lombok.Setter;
import kr.co.lupintech.model.user.User;

import javax.validation.constraints.*;

import java.time.LocalDate;

import static kr.co.lupintech.model.user.UserRole.ROLE_USER;

public class UserRequest {
    @Setter
    @Getter
    public static class LoginInDTO {
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식으로 작성해주세요")
        @NotEmpty
        private String email;
        @NotEmpty
        @Size(min = 4, max = 20)
        private String password;
    }

    @Setter
    @Getter
    public static class JoinInDTO {
        @Pattern(regexp = "^[가-힣]{2,10}$", message = "이름은 한글로 2~20자 이내로 작성해주세요")
        @NotEmpty
        private String username;

        @NotEmpty
        @Size(min = 4, max = 20)
        private String password;

        @NotEmpty
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식으로 작성해주세요")
        private String email;

        @NotEmpty
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜는 yyyy-MM-dd 형식으로 입력해주세요.")
        private String hireDate;

        public User toEntity() {
            LocalDate localDate = LocalDate.parse(hireDate);
            return User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .hireDate(localDate)
                    .role(ROLE_USER)
                    .status(true)
                    .remainDays(15)
                    .profile("https://lupinbucket.s3.ap-northeast-2.amazonaws.com/person.png")
                    .build();
        }
    }

    @Setter
    @Getter
    public static class ModifiedInDTO {

        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식으로 작성해주세요")
        @NotEmpty
        private String email;

        @Pattern(regexp = "^[가-힣]{2,10}$", message = "이름은 한글로 2~20자 이내로 작성해주세요")
        @NotEmpty
        private String username;

        private String newPassword;
        private String profileToDelete;

    }

    @Getter @Setter
    public static class AnnualInDTO{
        @NotNull
        @Min(0)
        @Max(25)
        private Integer remainDays;
    }

    @Getter @Setter
    public static class MasterInDTO {
        @NotNull
        private UserRole role;
    }
}
