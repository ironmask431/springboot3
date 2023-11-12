package kevin.study.springboot3.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//스프링 시큐리티의 UserDetails 를 구현
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    //유저 이름
    @Column(name = "nickname", unique = true)
    private String nickname;

    @Builder
    public User(Long id, String email, String password, String nickname) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    @Override //사용자의 권한목록을 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override //사용자의 패스워드 반환
    public String getPassword() {
        return password;
    }

    @Override //사용자의 이름(사용자 식별 고유값으로 설정해야함. (ex.로그인아이디) 여기선 email로 함)
    public String getUsername() {
        return email;
    }

    @Override //계정 만료되지 않았는지 확인
    public boolean isAccountNonExpired() {
        return true; // true = 만료되지 않음
    }

    @Override //계정 잠금 되지 않았는지 확인
    public boolean isAccountNonLocked() {
        return true; // true = 잠금되지 않음
    }

    @Override //패스워드 만료되지 않았는지 확인
    public boolean isCredentialsNonExpired() {
        return true; // true = 만료되지 않음
    }

    @Override //계정 사용가능한지 확인
    public boolean isEnabled() {
        return true; // true = 사용가능
    }

    //유저 이름 변경
    public User update(String nickname){
        this.nickname = nickname;
        return this;
    }
}
