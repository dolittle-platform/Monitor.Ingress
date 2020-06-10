package io.dolittle.moose.common.properties;

import io.dolittle.moose.common.properties.ping.PingIngressProperties;
import lombok.Value;
import lombok.experimental.NonFinal;

/**
 * Defines common properties that are shared by multiple modules to ensure they are configured the same at runtime.
 */
@Value
@NonFinal
public abstract class CommonProperties {
    protected PingIngressProperties ping;
}