package shop.mtcoding.restend.core;

import org.springframework.security.test.context.support.WithSecurityContext;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.model.user.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MyWithMockUserFactory.class)
public @interface MyWithMockUser {
    long id() default 1L;
    String username() default "cos";
    UserRole role() default UserRole.ROLE_USER;
    int remainDays() default 15;
}
