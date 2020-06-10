package io.dolittle.moose.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
  
@Configuration
@ComponentScan(basePackages = {"io.dolittle.moose.common", "io.dolittle.moose.kubernetes.config"})
@PropertySource(value = {"classpath:common.properties"})
public class CommonConfig {
}