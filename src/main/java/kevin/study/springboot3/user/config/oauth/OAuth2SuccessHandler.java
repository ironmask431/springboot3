package kevin.study.springboot3.user.config.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kevin.study.springboot3.user.config.jwt.TokenProvider;
import kevin.study.springboot3.user.domain.RefreshToken;
import kevin.study.springboot3.user.domain.User;
import kevin.study.springboot3.user.repository.RefreshTokenRepository;
import kevin.study.springboot3.user.service.UserService;
import kevin.study.springboot3.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

/**
 * OAuth 인증 성공시 실행될 핸들러
 * 스프링 시큐리티 기본로직은 successHandler 를 별도로 지정하지 않으면
 * 로그인 성공 후 SimpleUrlAuthenticationSuccessHandler 를 사용함.
 * 여기서는 토큰과 관련된 작업을 추가로 처리하기 위해 SimpleUrlAuthenticationSuccessHandler
 * 를 상속받은 후 onAuthenticationSuccess() 메소드를 오버라이딩 해줌.
 */
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
    public static final String REDIRECT_PATH = "/articles";

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    //oauth 인증 성공시 실행됨.
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        User user = userService.findByEmail((String) oAuth2User.getAttributes().get("email"));

        //리프레시 토큰 생성 -> DB에 저장 -> 쿠키에 저장
        //액세스토큰이 만료되면 리프레쉬토큰으로 재발급 요청이 가능하도록 쿠키에 저장해줌.
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        saveRefreshToken(user.getId(), refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);

        //액세스 토큰 생성 -> url param 에 액세스토큰 추가 (엑세스 토큰을 클라이언트에 전달하기 위함)
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        String targetUrl = getTargetUrl(accessToken);

        //OAuth 인증관련 설정값과  인증관련 쿠키 제거
        clearAuthenticationAttributes(request, response);

        //인증 성공 후 이동할 페이지로 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    //userId와 생성된 refreshToken을 받아서 있으면 update, 없으면 insert 실행
    @Transactional
    private void saveRefreshToken(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                                                          .map(entity -> entity.update(newRefreshToken))
                                                          .orElse(new RefreshToken(userId, newRefreshToken));
        refreshTokenRepository.save(refreshToken);
    }

    //생성된 refresh토큰을 response 쿠키에 추가한다.
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response,
                                         String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    //oauth 인증관련 설정값, 쿠키값 삭제
    //oauth 인증 프로세스를 진행하면서 세션과 쿠키에 임시로 저장해둔 인증관련 데이터를 제거한다.
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request); //기본 제공 메소드
        //쿠키에서 삭제하기 위해 아래 메소드 추가실행
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    //액세스 토큰을 url param에 추가
    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                                   .queryParam("token", token)
                                   .build()
                                   .toString();
    }

}
