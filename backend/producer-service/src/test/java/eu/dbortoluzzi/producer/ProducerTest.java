package eu.dbortoluzzi.producer;

import eu.dbortoluzzi.commons.model.Fragment;
import eu.dbortoluzzi.commons.utils.StringUtils;
import eu.dbortoluzzi.producer.config.InstanceConfiguration;
import eu.dbortoluzzi.producer.service.ProducerFragmentService;
import eu.dbortoluzzi.producer.service.ProducerPollingService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RunWith(SpringRunner.class)
@SpringBootTest()
@Slf4j
@Profile("test")
@ActiveProfiles("test")
public class ProducerTest {

	@Autowired
	ProducerPollingService producerPollingService;

	@Autowired
	ProducerFragmentService producerFragmentService;

	@Autowired
	InstanceConfiguration instanceConfiguration;

	private static String TEXT = "ECCO IL TESTO DA VERIFICARE";

	@Test
	public void contextLoads() {
		System.out.println("I'm alive " + instanceConfiguration.getInstanceName());

		Assert.assertTrue(instanceConfiguration.consumers().size() > 0);

		Fragment fragment = producerFragmentService.createFragment(1, 100, "INSTANCE", "FILENAME", TEXT.getBytes(StandardCharsets.UTF_8));
		Assert.assertTrue(producerFragmentService.isValidFragment(fragment));
		Assert.assertEquals(new String(producerFragmentService.decodeFragment(fragment)), TEXT);

		log.info("checksum: " + fragment.getPayload().getMetadata().getChecksum());
		log.info("HEX: " + fragment.getPayload().getText());

		log.info("JSON: " + new String(StringUtils.encodeHexString(producerFragmentService.toJsonString(fragment).getBytes(StandardCharsets.UTF_8))));
	}

	@Test
	@Ignore
	public void runPolling() throws IOException, InterruptedException {
		System.out.println("I'm alive " + instanceConfiguration.getInstanceName());
		producerPollingService.runPolling();
	}

}
