package io.dolittle.moose.common.properties;

import io.dolittle.moose.common.properties.ping.PingProperties;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@NonFinal
public abstract class CommonProperties {
    protected PingProperties ping;
}