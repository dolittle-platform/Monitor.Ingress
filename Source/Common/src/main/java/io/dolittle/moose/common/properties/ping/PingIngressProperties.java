// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.common.properties.ping;

import io.dolittle.moose.kubernetes.Annotation;
import io.dolittle.moose.kubernetes.ingresses.Ingress.Path;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Defines the configuration for the Ingresses that will be created to route HTTP ping requests back to Moose.
 */
@ConfigurationProperties(prefix = "io.dolittle.moose.ping")
@Data
public class PingIngressProperties {
    Annotation annotation;
    Path path;
}