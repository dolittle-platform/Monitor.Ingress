package io.dolittle.moose.controller;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import io.dolittle.moose.common.properties.CommonProperties;
import io.dolittle.moose.common.properties.ping.PingProperties;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "io.dolittle.moose")
@ConstructorBinding
public class ControllerProperties extends CommonProperties {
    
    public ControllerProperties(PingProperties ping) {
        super(ping);
    }
}