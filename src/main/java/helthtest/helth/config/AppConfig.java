package helthtest.helth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration //bean 정의 어노테이션
@EnableWebSecurity //security 활성화 시켜주는 어노테이션
public class AppConfig {
    @Bean //spring 관리받는
    public PasswordEncoder passwordEncoder()  { //bean 타입 생성
        return new BCryptPasswordEncoder(); //암호화
    }
}
