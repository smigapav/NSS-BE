package cz.cvut.fel.ear.reservation_system.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {

    @Value("${hazelcast.timeToLiveSeconds}")
    private int timeToLiveSeconds;

    @Bean
    public Config hazelCastConfig() {
        return new Config()
                .setInstanceName("hazelcast-instance")
                .addMapConfig(
                        new MapConfig()
                                .setName("rooms")
                                .setTimeToLiveSeconds(timeToLiveSeconds));
    }
}
