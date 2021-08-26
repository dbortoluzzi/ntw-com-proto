package eu.dbortoluzzi.producer;

import eu.dbortoluzzi.producer.service.ProducerPollingService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.TimeZone;

@SpringBootApplication
public class ProducerServiceApplication {

	@Autowired
    ProducerPollingService producerPollingService;

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer initJackson() {
		return builder -> builder.timeZone(TimeZone.getDefault());
	}


	public static void main(String[] args) {
		ConfigurableApplicationContext context = 
				SpringApplication.run(ProducerServiceApplication.class, args);

		ProducerServiceApplication app =
				context.getBean(ProducerServiceApplication.class);
		app.init();
	}
	
	@SneakyThrows
	public void init() {
		producerPollingService.runPolling();
	}
}
