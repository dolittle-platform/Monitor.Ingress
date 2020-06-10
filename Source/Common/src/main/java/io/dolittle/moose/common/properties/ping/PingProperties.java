package io.dolittle.moose.common.properties.ping;

import io.dolittle.moose.kubernetes.Annotation;
import io.dolittle.moose.kubernetes.ingresses.Ingress.Path;
import lombok.Value;

@Value
public class PingProperties {
    private final Annotation annotation;
    private final Path path;
}