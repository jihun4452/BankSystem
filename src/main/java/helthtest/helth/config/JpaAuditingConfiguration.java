package helthtest.helth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing //Entity 값의 생성 변경시 자동 값 할당
@Configuration
public class JpaAuditingConfiguration {
}
