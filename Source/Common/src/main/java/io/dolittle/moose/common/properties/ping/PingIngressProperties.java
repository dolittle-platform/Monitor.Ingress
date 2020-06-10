package io.dolittle.moose.common.properties.ping;

import io.dolittle.moose.kubernetes.Annotation;
import io.dolittle.moose.kubernetes.ingresses.Ingress.Path;
import lombok.Value;

/**
 * Defines the configuration for the Ingresses that will be created to route HTTP ping requests back to Moose.
 */
@Value
public class PingIngressProperties {
    private final Annotation annotation;
    private final Path path;
}