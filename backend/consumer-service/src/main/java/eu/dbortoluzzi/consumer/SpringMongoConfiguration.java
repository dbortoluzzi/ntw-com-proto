package eu.dbortoluzzi.consumer;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import eu.dbortoluzzi.consumer.config.InstanceConfiguration;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import java.net.InetAddress;

@Configuration
@Profile("default")
public class SpringMongoConfiguration extends AbstractMongoConfiguration {

    @Autowired
    InstanceConfiguration instanceConfiguration;

    @SneakyThrows
    @Override
    protected String getDatabaseName() {
        return "db_"+instanceConfiguration.getInstanceName();
    }

    @Value("${spring.data.mongodb.uri}")
    private String mongoURI;

    @Override
    public MongoClient mongoClient() {
        return new MongoClient(new MongoClientURI(mongoURI));
    }
}