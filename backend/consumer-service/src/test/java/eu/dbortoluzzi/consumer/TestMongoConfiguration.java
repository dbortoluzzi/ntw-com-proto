package eu.dbortoluzzi.consumer;

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;


@Configuration
@Profile("test")
@EnableAutoConfiguration(exclude = { MongoDataAutoConfiguration.class })
public class TestMongoConfiguration extends AbstractMongoConfiguration {

    private static final String LOCALHOST = "127.0.0.1";
    private static final String DB_NAME = "dbtest";
    private static final int MONGO_TEST_PORT = 27028;

    @Override
    protected String getDatabaseName() {
        return "TEST";
    }

    @SneakyThrows
    @Override
    public MongoClient mongoClient() {
        MongodStarter starter = MongodStarter.getDefaultInstance();
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.V3_3)
                .net(new Net(LOCALHOST, MONGO_TEST_PORT, Network.localhostIsIPv6()))
                .build();

        MongodExecutable mongodExecutable = null;
        mongodExecutable = starter.prepare(mongodConfig);
        MongodProcess mongoProcess = mongodExecutable.start();
        MongoClient mongo = new MongoClient(LOCALHOST, MONGO_TEST_PORT);
        mongo.getDB(DB_NAME);
        return mongo;
    }
}
