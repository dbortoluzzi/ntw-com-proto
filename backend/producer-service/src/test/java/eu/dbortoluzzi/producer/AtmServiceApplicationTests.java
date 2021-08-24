package eu.dbortoluzzi.producer;

import eu.dbortoluzzi.producer.config.InstanceConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class AtmServiceApplicationTests {

	@Autowired
	ProducerPollingService producerPollingService;

	@Autowired
	InstanceConfiguration instanceConfiguration;

	@Test
	public void contextLoads() throws IOException, InterruptedException {
		System.out.println("I'm alive " + instanceConfiguration.getInstanceName());

		producerPollingService.runPolling();
	}

}
