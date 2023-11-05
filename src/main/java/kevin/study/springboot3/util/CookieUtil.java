package kevin.study.springboot3.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

public class CookieUtil {

    //요청값(이름, 값, 만료 기간)으로 http response 에 쿠키 추가
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    //쿠키의 이름을 입력받아 쿠키 삭제
    //쿠키를 실제로 삭제할 순 없으므로, 해당이름의 쿠키의 value를 빈값으로, 만료시간을0으로 설정한다.
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }

    //객체를 직렬화해 쿠키의 값으로 변환
    // (Object객체 -> byte[] -> Base64 encode -> 쿠키(String))
    public static String serialize(Object object) {
        //object객체를 byte[]로 직렬화
        byte[] bytes = SerializationUtils.serialize(object);

        //byte[] 를 Base64 String으로 encode(직렬화)
        return Base64.getUrlEncoder().encodeToString(bytes);
    }

    //쿠키를 역직렬화해 객체로 변환
    // 쿠키value(String) -> base64 decode -> byte[] -> Object객체 -> class
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        //쿠키의 value를 byte[]로 Base64 decode
        byte[] bytes = Base64.getUrlDecoder().decode(cookie.getValue());

        //byte[]를 Object로 역직렬화
        Object object = SerializationUtils.deserialize(bytes);

        //Object를 class로 변환
        return cls.cast(object);
    }
}
