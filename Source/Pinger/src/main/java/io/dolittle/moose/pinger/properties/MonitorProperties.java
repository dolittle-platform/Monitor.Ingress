// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.pinger.properties;

import io.dolittle.moose.kubernetes.Annotation;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "io.dolittle.moose.monitor")
@Data
public class MonitorProperties {
    private String _key;
    private String _value;

    public Annotation getAnnotation() {
        return new Annotation(_key, _value);
    }

}
