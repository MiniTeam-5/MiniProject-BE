package kr.co.lupintech.core;

import org.springframework.security.test.context.support.WithSecurityContext;
import kr.co.lupintech.model.user.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MyWithMockUserFactory.class)
public @interface MyWithMockUser {
    long id() default 1L;
    String username() default "박코스";
    String email() default "cos@nate.com";
    UserRole role() default UserRole.ROLE_USER;
    int remainDays() default 15;
}
