package com.cloudglasses.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories({"com.cloudglasses.repository", "com.cloudglasses.model"})
public class EntityConfig {
}
