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

        //세션 정책 설정 : 세션을 비활성화 시킨다.
//        SessionCreationPolicy.Always : 스프링 시큐리티가 항상 세션 생성
//        SessionCreationPolicy.IF_REQUIRED : 스프링 시큐리티가 필요 시 생성(default)
//        SessionCreationPolicy.Never : 스프링 시큐리티가 생성하지 않지만 이미 존재하면 사용
//        SessionCreationPolicy.Stateless: 스프링 시큐리티가 생성하지 않고 존재해도 사용하지 않음.
//        → 않음JWT 토큰방식을 사용할 때는 Stateless 정책을 사용한다.
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
            .authorizationEndpoint() // 인가 엔드포인트..?
            //쿠키에서 OAuth 인증정보 가져오기, 인증정보 쿠키에 저장하기를 수행..
            .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
            .and()
            .successHandler(oAuth2SuccessHandler()) //인증성공시 실행할 핸들러 - 리프레시, 액세스토큰발급, 쿠키저장, redirect 수행
            .userInfoEndpoint() // 인증된 사용자의 클레임/속성에 접근할때 사용하는 엔드포인트,,?
            .userService(oAuth2UserCustomService); //인증성공 시 oauth로 부터받은 유저정보를 객체로 저장하는 서비스 저장

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
