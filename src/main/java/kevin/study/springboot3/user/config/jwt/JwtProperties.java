package kevin.study.springboot3.user.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties("jwt") // application.yml의 "jwt" 프로퍼티값을 가져옴
public class JwtProperties {
    private String issuer;
    private String secretKey;
}
