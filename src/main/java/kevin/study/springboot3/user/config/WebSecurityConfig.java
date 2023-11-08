package kevin.study.springboot3.user.config;

import kevin.study.springboot3.user.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

//JWT + Oauth2 로그인 방식을 사용하려면 기존 스프링시큐리티 로그인 설정과 다른 설정을 사용해야함.
//아래 코드는 모두 주석처리


//스프링 시큐리티 설정은 버전별로 설정에 차이가 있음.
//현재코드는 springBoot 3.0.2 버전기준이고 3.1.2 버전에서는 구동시 에러발생함.
//@Configuration //@Bean 이 등록된 객체들을 스프링컨테이너에 등록하기 위해 설정.
//@RequiredArgsConstructor
//public class WebSecurityConfig {
//    private final UserDetailService userDetailService;
//
//    //시큐리티 기능 비활성화 적용
//    @Bean
//    public WebSecurityCustomizer configure() {
//        return (web) -> web.ignoring()
//                           .requestMatchers(toH2Console()) //h2console 페이지
//                           .requestMatchers("/static/**"); //정적 리소스 경로 (이미지, HTML 등)
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http.authorizeRequests() //인증, 인가 설정
//                   .requestMatchers("/login", "/signup", "/user").permitAll() //해당경로는 인증,인가 없이 접근가능
//                   .anyRequest().authenticated() // 그외 페이지 설정 - 인증이 필요함.
//                   .and()
//                   .formLogin() //form 로그인 사용
//                   .loginPage("/login")//로그인 페이지 경로 설정
//                   .defaultSuccessUrl("/articles", true) // true 추가해주지 않으면 로그인후 해당 URL로 이동이 안됨.
//                   .and()
//                   .logout() // 로그아웃 설정
//                   .logoutSuccessUrl("/login") //로그아웃 성공시 이동할 url
//                   .invalidateHttpSession(true) //로그아웃 후 세션 전체 삭제 설정
//                   .and()
//                   .csrf().disable() //CSRF 공격 방지기능 off
//                   .build();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder) throws Exception {
//        return http.getSharedObject(AuthenticationManagerBuilder.class)
//                   .userDetailsService(userDetailService) // 사용자 정보를 가져올 서비스 설정. UserDetailsService를 구현한 클래스여야함.
//                   .passwordEncoder(bCryptPasswordEncoder) //비밀번호 암호화를 위한 인코더 설정.
//                   .and()
//                   .build();
//    }
//
//    //bCryptPasswordEncoder 빈 등록
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
