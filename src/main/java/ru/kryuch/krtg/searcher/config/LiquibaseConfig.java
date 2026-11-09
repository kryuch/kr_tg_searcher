package ru.kryuch.krtg.searcher.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {

    @Bean
    public ApplicationRunner liquibaseRunner(DataSource dataSource) {
        return args -> {
            SpringLiquibase liquibase = new SpringLiquibase();
            liquibase.setDataSource(dataSource);
            liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.xml");
            // Проверяем, не запущен ли уже Liquibase
            liquibase.setShouldRun(false); // или проверять DATABASECHANGELOG
            liquibase.afterPropertiesSet();
        };
    }
}