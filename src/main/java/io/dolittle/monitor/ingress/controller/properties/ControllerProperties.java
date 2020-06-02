// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.monitor.ingress.controller.properties;

import io.dolittle.monitor.ingress.controller.model.LabelSelector;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "monitor.ingress.controller")
@Data
@EqualsAndHashCode(callSuper = true)
public class ControllerProperties extends LabelSelector {


}
