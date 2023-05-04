package shop.mtcoding.restend.core;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MyWithMockUserFactory implements WithSecurityContextFactory<MyWithMockUser> {
    @Override
    public SecurityContext createSecurityContext(MyWithMockUser mockUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        User user = User.builder()
                .id(mockUser.id())
                .username(mockUser.username())
                .password("1234")
                .email(mockUser.username()+"@nate.com")
                .role(UserRole.USER)
                .status(true)
                .hireDate(LocalDate.now().minusYears(1).minusWeeks(1))
                .remainDays(mockUser.remainDays())
                .createdAt(LocalDateTime.now())
                .build();
        MyUserDetails myUserDetails = new MyUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(myUserDetails, myUserDetails.getPassword(), myUserDetails.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
