package eu.dbortoluzzi.producer;

import eu.dbortoluzzi.producer.service.ProducerPollingService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ProducerServiceApplication {

	@Autowired
    ProducerPollingService producerPollingService;

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
