package eu.dbortoluzzi.consumer;

import eu.dbortoluzzi.consumer.service.ConsumerSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
@EnableSwagger2
public class AtmServiceApplication {

	@Autowired
	private AtmDataSeeder atmDataSeeder;

	@Autowired
	private ConsumerSyncService consumerSyncService;
	
	public static void main(String[] args) {
		ConfigurableApplicationContext context = 
				SpringApplication.run(AtmServiceApplication.class, args);
		
		AtmServiceApplication app =
				context.getBean(AtmServiceApplication.class);
		app.init();
	}
	
	public void init() {
		atmDataSeeder.seedIfEmpty();

		TimerTask task = new TimerTask() {
			public void run() {
				consumerSyncService.syncProcess(new Date());
			}
		};
		Timer timer = new Timer("Timer");

		long delay = 10000L;
		timer.scheduleAtFixedRate(task, delay, delay);
	}

}
