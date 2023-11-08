package kevin.study.springboot3.user.config.oauth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kevin.study.springboot3.util.CookieUtil;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

/**
 * Oauth2에 필요한 정보를 세션이 아닌 쿠키에 저장해서 쓸 수 있도록
 * 인증요청과 관련된 상태를 저장할 저장소 구현.
 * 권한 인증 흐름에서 클라이언트의 요청을 유지하는데 사용하는 AuthorizationRequestRepository
 * 클래스를 구현해 쿠키를 사용하여 OAuth의 정보를 가져오고 저장하는 로직을 구현한다.
 */
public class OAuth2AuthorizationRequestBasedOnCookieRepository
        implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public final static String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    public final static int COOKIE_EXPIRE_SECOND = 18000; //30분

    //request 에서 oauth2_auth_request 쿠키값을 받은 후 Oauth2 인증객체로 변환.
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        return CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class);
    }

    //OAuth2 인증객체를 받아서.. NULL이면 oauth2_auth_request 쿠키를 삭제
    //있으면 OAuth2 인증객체를 직렬화하여 oauth2_auth_request 쿠키로 저장.
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                         HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(request, response);
            return;
        }
        CookieUtil.addCookie(response,
                OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                CookieUtil.serialize(authorizationRequest),
                COOKIE_EXPIRE_SECOND);
    }

    //? 잘모르겟음. remove시 왜 load를 다시하는지..
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
                                                                 HttpServletResponse response) {
        return this.loadAuthorizationRequest(request);
    }

    //oauth2_auth_request 쿠키를 삭제
    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
    }
}
