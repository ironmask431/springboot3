package kevin.study.springboot3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // create_at, updated_at 자동 업데이트 위해 추가필요
public class Springboot3Application {
	public static void main(String[] args) {
		SpringApplication.run(Springboot3Application.class, args);
	}

}
