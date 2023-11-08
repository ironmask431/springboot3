package kevin.study.springboot3.user.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * OncePerRequestFilter : 1번의 request에 filter 처리를 하는 클래스
 * 요청의 헤더값의 토큰을 확인하여 유효하면 securityContextHolder 에 인증정보를 저장함.
 * securityContext 는 인증 객체를 저장하는 보관소임. 인증정보를 저장, 조회할 수 있다.
 * securityContext 객체를 저장하는 객체가 securityContextHolder.
 * Http Request 에서 액세스토큰값이 담긴 Authorization 헤더값을 가져온 뒤 엑세스토큰이 유효하다면 인증정보를 저장함.
 */
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final static String HEADER_AUTHRIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //요청 헤더에서 Authorization 값을 조회한다.
        String authorizationHeader = request.getHeader(HEADER_AUTHRIZATION);

        //접두사 제거
        String token = getAccessToken(authorizationHeader);

        //토큰 유효성 검사, 유효하면 인증정보 설정
        if (tokenProvider.validToken(token)) {
            SecurityContextHolder.getContext().setAuthentication(
                    tokenProvider.getAuthentication(token)
            );
        }
        filterChain.doFilter(request, response);
    }

    //authorizationHeader 의 접두사 제거("Bearer " 제거 )
    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
