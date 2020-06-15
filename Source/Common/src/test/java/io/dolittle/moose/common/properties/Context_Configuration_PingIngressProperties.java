// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.common.properties;

import io.dolittle.moose.common.properties.ping.PingIngressProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@TestConfiguration
@ComponentScan(basePackages = {"io.dolittle.moose.common.properties"})
@EnableConfigurationProperties(value = {PingIngressProperties.class})
@PropertySource(value ={"classpath:test.properties"})
public class Context_Configuration_PingIngressProperties {
}
