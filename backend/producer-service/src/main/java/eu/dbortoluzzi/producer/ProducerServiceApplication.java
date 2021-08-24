package eu.dbortoluzzi.producer;

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
	
	public void init() {
//		producerPollingService.runPollingFile();
	}
}
