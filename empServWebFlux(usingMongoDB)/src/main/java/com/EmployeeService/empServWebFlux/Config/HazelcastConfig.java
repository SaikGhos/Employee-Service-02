package com.EmployeeService.empServWebFlux.Config;

import com.EmployeeService.empServWebFlux.Entity.EmployeeMain;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {
    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        return Hazelcast.newHazelcastInstance(config);
    }

    @Bean
    public IMap<Integer, EmployeeMain> userCache(HazelcastInstance hazelcastInstance) {
        return hazelcastInstance.getMap("user-cache");
    }
}
