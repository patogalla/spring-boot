package com.patogalla.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;

@SpringBootApplication
@EnableConfigurationProperties
@Import(BeanValidatorPluginsConfiguration.class)
public class PatogallaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatogallaApiApplication.class, args);
    }

}
