package eu.dbortoluzzi.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableSwagger2
public class AuthServiceApplication {

	@Autowired
	private AuthDataSeeder authDataSeeder;
	
	public static void main(String[] args) {
		ConfigurableApplicationContext context = 
				SpringApplication.run(AuthServiceApplication.class, args);

		AuthServiceApplication app =
				context.getBean(AuthServiceApplication.class);
		app.init();
	}


	@Bean
	public Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("eu.dbortoluzzi.auth")).build();
	}
	
	public void init() {
		authDataSeeder.seedIfEmpty();
	}
}
