package com.phcworld.boardanswerservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;

@Slf4j
@Configuration
public class DataSourceConfig {

    private static final String MASTER_SERVER = "master";
    private static final String SLAVE_SERVER = "slave";

    @Bean
    @Qualifier(MASTER_SERVER)
    @ConfigurationProperties("spring.datasource.master")
    public DataSource masterDataSource() {
        log.info("source register");
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Qualifier(SLAVE_SERVER)
    @ConfigurationProperties("spring.datasource.slave")
    public DataSource slaveDataSource() {
        log.info("replica register");
        return DataSourceBuilder.create().build();
    }

    @Bean
    public DataSource routingDataSource(@Qualifier(MASTER_SERVER) DataSource masterDataSource,
                                        @Qualifier(SLAVE_SERVER) DataSource slaveDataSource) {

        RoutingDataSource routingDataSource = new RoutingDataSource();

        HashMap<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(MASTER_SERVER, masterDataSource);
        dataSourceMap.put(SLAVE_SERVER, slaveDataSource);

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(masterDataSource);

        return routingDataSource;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        DataSource determinedDataSource = routingDataSource(masterDataSource(), slaveDataSource());
        return new LazyConnectionDataSourceProxy(determinedDataSource);
    }

}
