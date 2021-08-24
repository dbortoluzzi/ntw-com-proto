package eu.dbortoluzzi.auth;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import java.net.InetAddress;

@Configuration
@Profile("default")
public class SpringMongoConfiguration extends AbstractMongoConfiguration {

    @SneakyThrows
    @Override
    protected String getDatabaseName() {
        InetAddress localHost = InetAddress.getLocalHost();
        return "db_"+localHost.getHostName();
    }

    @Value("${spring.data.mongodb.uri}")
    private String mongoURI;

    @Override
    public MongoClient mongoClient() {
        return new MongoClient(new MongoClientURI(mongoURI));
    }
}