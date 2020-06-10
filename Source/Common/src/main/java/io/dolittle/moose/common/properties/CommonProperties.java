// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

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