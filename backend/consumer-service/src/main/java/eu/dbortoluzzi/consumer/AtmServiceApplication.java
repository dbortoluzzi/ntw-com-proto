package eu.dbortoluzzi.consumer;

import eu.dbortoluzzi.consumer.config.InstanceConfiguration;
import eu.dbortoluzzi.consumer.service.ConsumerSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;

@SpringBootApplication
@EnableSwagger2
@Slf4j
public class AtmServiceApplication {

	public static final long DELAY = 5000L;
	private boolean firstSync = true;

	@Autowired
	private InstanceConfiguration instanceConfiguration;

	@Autowired
	private ConsumerSyncService consumerSyncService;

//	@Bean
//	public Executor asyncExecutor() {
//		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//		executor.setCorePoolSize(15);
//		executor.setMaxPoolSize(15);
//		executor.setQueueCapacity(500);
//		executor.setThreadNamePrefix("CONSUMER-");
//		executor.initialize();
//		return executor;
//	}
	
	public static void main(String[] args) {
		ConfigurableApplicationContext context = 
				SpringApplication.run(AtmServiceApplication.class, args);
		
		AtmServiceApplication app =
				context.getBean(AtmServiceApplication.class);
		app.init();
	}
	
	public void init() {
		log.info("starting for instance {}", instanceConfiguration.getInstanceName());

		TimerTask task = new TimerTask() {
			public void run() {
				// TODO: add mutual LOCK with quartz
				consumerSyncService.syncProcess(new Date(), firstSync);
				firstSync = false;
			}
		};
		Timer timer = new Timer("Timer");

		long delay = DELAY;
		timer.scheduleAtFixedRate(task, delay, delay);
	}

}
