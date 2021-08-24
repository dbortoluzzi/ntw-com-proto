package eu.dbortoluzzi.producer;

import eu.dbortoluzzi.commons.model.Fragment;
import eu.dbortoluzzi.producer.config.InstanceConfiguration;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class ProducerTest {

	@Autowired
	ProducerPollingService producerPollingService;

	@Autowired
	ProducerFragmentService producerFragmentService;

	@Autowired
	InstanceConfiguration instanceConfiguration;

	private static String TEXT = "ECCO IL TESTO DA VERIFICARE";

	@Test
	public void contextLoads() throws IOException, InterruptedException {
		System.out.println("I'm alive " + instanceConfiguration.getInstanceName());

		Fragment fragment = producerFragmentService.createFragment(1, 100, "INSTANCE", TEXT.getBytes(StandardCharsets.UTF_8));
		Assert.assertTrue(producerFragmentService.isValidFragment(fragment));
		Assert.assertEquals(new String(producerFragmentService.decodeFragment(fragment)), TEXT);
	}

	@Test
	@Ignore
	public void runPolling() throws IOException, InterruptedException {
		System.out.println("I'm alive " + instanceConfiguration.getInstanceName());
		producerPollingService.runPolling();
	}

}
