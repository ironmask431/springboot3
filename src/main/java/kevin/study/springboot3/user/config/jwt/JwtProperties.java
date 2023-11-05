package kevin.study.springboot3.user.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//application.yml 에 설정한
//프로퍼티값이 클래스에 설정되려면 @Setter가 반드시 있어야 한다.
@Setter
@Getter
@Component
@ConfigurationProperties("jwt") // application.yml의 "jwt" 프로퍼티값을 읽어옴
public class JwtProperties {
    private String issuer;
    private String secretKey;
}
