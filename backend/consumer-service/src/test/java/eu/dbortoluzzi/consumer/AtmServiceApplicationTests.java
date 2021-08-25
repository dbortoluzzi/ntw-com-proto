package eu.dbortoluzzi.consumer;

import eu.dbortoluzzi.commons.model.RoutingElement;
import eu.dbortoluzzi.consumer.config.InstanceConfiguration;
import eu.dbortoluzzi.consumer.model.Address;
import eu.dbortoluzzi.consumer.model.AtmIndexable;
import eu.dbortoluzzi.consumer.repository.AtmsRepository;
import eu.dbortoluzzi.consumer.repository.AtmsRepositoryCustom;
import eu.dbortoluzzi.consumer.service.ConsumerSyncService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest()
@Slf4j
@Profile("test")
@ActiveProfiles("test")
public class AtmServiceApplicationTests {
	@Autowired
	AtmsRepositoryCustom atmsRepositoryCustom;

	@Autowired
	AtmsRepository atmsRepository;

	@Autowired
	ConsumerRoutingTable consumerRoutingTable;

	@Autowired
	ConsumerSyncService consumerSyncService;

	@Autowired
	InstanceConfiguration instanceConfiguration;

	@Test
	public void contextLoads() {
		log.info("contextLoads");
		atmsRepository.insert(new AtmIndexable(0, "ING", new Address("A", "B", "C", "D", null)));
		Page<AtmIndexable> page = atmsRepositoryCustom.search("ING", PageRequest.of(0, 100));
		Assert.assertEquals(page.getTotalElements(), 1);
		Assert.assertEquals(page.getContent().get(0).getType(), "ING");
	}

	@Test
	public void sync() {
		log.info("sync");
		Assert.assertTrue(consumerRoutingTable.getRoutingTable().size() > 0);
		for (RoutingElement routingElement: consumerRoutingTable.getRoutingTable()) {
			log.info(routingElement.toString());
		}
		for (RoutingElement routingElement: instanceConfiguration.otherConsumers()) {
			String url = consumerSyncService.consumerFragmentUrl(routingElement);
			log.info("fragment url: {}", url);
		}
	}
}
