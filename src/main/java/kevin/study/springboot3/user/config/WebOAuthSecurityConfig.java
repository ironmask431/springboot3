package kevin.study.springboot3.user.config;


import kevin.study.springboot3.user.config.jwt.TokenAuthenticationFilter;
import kevin.study.springboot3.user.config.jwt.TokenProvider;
import kevin.study.springboot3.user.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import kevin.study.springboot3.user.config.oauth.OAuth2SuccessHandler;
import kevin.study.springboot3.user.config.oauth.OAuth2UserCustomService;
import kevin.study.springboot3.user.repository.RefreshTokenRepository;
import kevin.study.springboot3.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@RequiredArgsConstructor
public class WebOAuthSecurityConfig {
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    //스프링 시큐리티 비활성화 경로 설정
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                           .requestMatchers(toH2Console()) //h2console 페이지
                           .requestMatchers("/static/**"); //정적 리소스 경로 (이미지, HTML 등)
    }

    //토큰 인증방식을 사용할 것이므로, 기존 폼로그인, 로그아웃 비활성화 시킨다.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
            .disable()
            .httpBasic()
            .disable()
            .formLogin()
            .disable()
            .logout()
            .disable();

        //세션을 비활성화 시킨다.
        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //헤더를 확인할 커스텀 필터 추가
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        //토큰 재발급 요청 api url은 인증없이 접근 가능하도록 설정. 나머지 api url은 인증필요.
        http.authorizeHttpRequests()
            .requestMatchers("/api/token").permitAll()
            .requestMatchers("/api/*").authenticated()
            .anyRequest().permitAll();

        //oauth2 로그인 페이지 설정
        http.oauth2Login()
            .loginPage("/login")
            .authorizationEndpoint()
            //authorization 요청과 관련된 상태를 쿠키에 저장?
            .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
            .and()
            .successHandler(oAuth2SuccessHandler()) //인증성공시 실행할 핸들러
            .userInfoEndpoint()
            .userService(oAuth2UserCustomService);

        http.logout()
            .logoutSuccessUrl("/login");

        // "/api/**" 로 시작하는 url 인 경우 401 상태코드를 반환하도록 예외처리 ?
        http.exceptionHandling()
            .defaultAuthenticationEntryPointFor(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                    new AntPathRequestMatcher("/api/**"));

        return http.build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenProvider, refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService);
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    //bCryptPasswordEncoder 빈 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
