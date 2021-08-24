package eu.dbortoluzzi.consumer;

import eu.dbortoluzzi.consumer.model.Address;
import eu.dbortoluzzi.consumer.model.AtmIndexable;
import eu.dbortoluzzi.consumer.repository.AtmsRepository;
import eu.dbortoluzzi.consumer.repository.AtmsRepositoryCustom;
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

	@Test
	public void contextLoads() {
		log.info("contextLoads");
		atmsRepository.insert(new AtmIndexable(0, "ING", new Address("A", "B", "C", "D", null)));
		Page<AtmIndexable> page = atmsRepositoryCustom.search("ING", PageRequest.of(0, 100));
		Assert.assertEquals(page.getTotalElements(), 1);
		Assert.assertEquals(page.getContent().get(0).getType(), "ING");
	}
}
