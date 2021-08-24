package eu.dbortoluzzi.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class AtmServiceApplication {

	@Autowired
	private AtmDataSeeder atmDataSeeder;
	
	public static void main(String[] args) {
		ConfigurableApplicationContext context = 
				SpringApplication.run(AtmServiceApplication.class, args);
		
		AtmServiceApplication app =
				context.getBean(AtmServiceApplication.class);
		app.init();
	}
	
	public void init() {
		atmDataSeeder.seedIfEmpty();
	}
}
