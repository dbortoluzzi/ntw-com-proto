package eu.dbortoluzzi.consumer;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import eu.dbortoluzzi.consumer.model.Address;
import eu.dbortoluzzi.consumer.model.AtmIndexable;
import eu.dbortoluzzi.consumer.repository.AtmsRepository;
import eu.dbortoluzzi.consumer.repository.AtmsRepositoryCustom;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class AtmServiceApplicationTests {

	private static final String LOCALHOST = "127.0.0.1";
	private static final String DB_NAME = "dbtest";
	private static final int MONGO_TEST_PORT = 27028;
	private static MongodProcess mongoProcess;
	private static Mongo mongo;

	@Autowired
	AtmsRepositoryCustom atmsRepositoryCustom;

	@Autowired
	AtmsRepository atmsRepository;

	@BeforeClass
	public static void initializeDB() throws IOException {
		MongodStarter starter = MongodStarter.getDefaultInstance();
		IMongodConfig mongodConfig = new MongodConfigBuilder()
				.version(Version.Main.V3_3)
				.net(new Net(LOCALHOST, MONGO_TEST_PORT, Network.localhostIsIPv6()))
				.build();

		MongodExecutable mongodExecutable = null;
		try {
			mongodExecutable = starter.prepare(mongodConfig);
			mongoProcess = mongodExecutable.start();
			mongo = new MongoClient(LOCALHOST, MONGO_TEST_PORT);
			mongo.getDB(DB_NAME);
		} finally {
			if (mongodExecutable != null)
				mongodExecutable.stop();
		}
	}

	@Test
	public void contextLoads() {
		atmsRepository.insert(new AtmIndexable(0, "ING", new Address("A", "B", "C", "D", null)));
		Page<AtmIndexable> page = atmsRepositoryCustom.search("ING", PageRequest.of(0, 100));
		Assert.assertEquals(page.getTotalElements(), 1);
		Assert.assertEquals(page.getContent().get(0).getType(), "ING");
	}

	@AfterClass
	public static void shutdownDB() throws InterruptedException {
		if (mongo != null) mongo.close();
		if (mongoProcess != null) mongoProcess.stop();
	}
}
