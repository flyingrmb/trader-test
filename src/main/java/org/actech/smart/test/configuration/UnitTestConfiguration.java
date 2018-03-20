package org.actech.smart.test.configuration;

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongoCmdOptionsBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created by paul on 2018/3/10.
 */
@Configuration
public class UnitTestConfiguration {
    @Profile("dev")
    @Bean
    public MongoClient embeddedMongoClient() throws IOException {
        MongodStarter runtime = MongodStarter.getDefaultInstance();
        IMongodConfig config = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .cmdOptions(new MongoCmdOptionsBuilder().useNoJournal(false).build())
                .shardServer(true)
                .build();

        MongodExecutable mongodExe = runtime.prepare(config);
        mongodExe.start();

        MongoClient mongo = new MongoClient(config.net().getServerAddress().getHostName(),
                config.net().getPort());
        return mongo;
    }

    @Bean
    public DataInitializer dataInitializer() {
        return new DataInitializer();
    }

    class DataInitializer {
        @PostConstruct
        public void afterPropertiesSet() {
            System.out.println("Hello world!");
        }
    }
}
