package shop.mtcoding.restend.core.auth.session;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import shop.mtcoding.restend.model.user.User;

import java.util.ArrayList;
import java.util.Collection;

@Setter
@Getter
public class MyUserDetails implements UserDetails {
    private User user;

    public MyUserDetails(User user) {
        this.user = user;
    }

    /**
     * user 의 role 타입이 string -> enum 으로 변경 됨에 따른 코드 수정
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collector = new ArrayList<>();
        collector.add(() -> user.getRole().toString());
        return collector;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
