package com.random.challenge.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;


@Configuration
@Slf4j
public class H2Config {

    public DataSource dataSource(){
        log.info("Creating Database Connection");
        return DataSourceBuilder.create()
                .driverClassName("org.h2.Driver")
                .url("jdbc:h2:mem:general;DB_CLOSE_DELAY=-1;MODE=PostgreSQL")
                .username("standard_user")
                .password("password1234")
                .build();
    }

}
