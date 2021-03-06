package eu.dbortoluzzi.consumer;

import eu.dbortoluzzi.commons.model.RoutingElement;
import eu.dbortoluzzi.consumer.config.InstanceConfiguration;
import eu.dbortoluzzi.consumer.model.StatisticsCounter;
import eu.dbortoluzzi.consumer.repository.FragmentRepository;
import eu.dbortoluzzi.consumer.service.ConsumerSyncService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest()
@Slf4j
@Profile("test")
@ActiveProfiles("test")
public class ConsumerServiceApplicationTests {
	@Autowired
	ConsumerRoutingTable consumerRoutingTable;

	@Autowired
	ConsumerSyncService consumerSyncService;

	@Autowired
	InstanceConfiguration instanceConfiguration;

	@Autowired
	FragmentRepository fragmentRepository;

	@Test
	public void contextLoads() {
		log.info("contextLoads");
	}

	@Test
	public void testStatistics() {
		Calendar startCal = Calendar.getInstance();
		startCal.set(Calendar.MINUTE, startCal.get(Calendar.MINUTE) - 10);

		List<StatisticsCounter> statisticsCounters = fragmentRepository.countFragmentFiltered(startCal.getTime(), new Date(), 60L, MongoDbCriteriaUtils.producersInFilter(Collections.singletonList("localhost")));
		List<StatisticsCounter> statisticsCounters2 = fragmentRepository.countFragmentFiltered(startCal.getTime(), new Date(), 60L, MongoDbCriteriaUtils.consumersInOrNotFilter(Collections.singletonList("localhost")).andOperator(MongoDbCriteriaUtils.producersInFilter(Collections.singletonList("localhost"))));
	}

	@Test
	public void sync() {
		log.info("sync");
		Assert.assertTrue(consumerRoutingTable.getRoutingTable().size() > 0);
		for (RoutingElement routingElement: consumerRoutingTable.getRoutingTable()) {
			log.info(routingElement.toString());
		}
		for (RoutingElement routingElement: instanceConfiguration.otherConsumers()) {
			String url = consumerSyncService.prepareConsumerFragmentUrl(routingElement, "CHECKSUM");
			log.info("fragment url: {}", url);
		}
	}
}
