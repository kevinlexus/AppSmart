package com.ast.app.service;

import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan({"com.ast.app"})
@EntityScan(basePackages = {"com.ast.app.model"})
@EnableJpaRepositories(basePackages = "com.ast.app.repository")
public class Config {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();

    }
}
