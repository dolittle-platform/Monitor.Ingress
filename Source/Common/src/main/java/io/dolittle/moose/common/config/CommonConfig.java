// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
  
@Configuration
@ComponentScan(basePackages = {"io.dolittle.moose.common", "io.dolittle.moose.kubernetes.config"})
@PropertySource(value = {"classpath:common.properties"})
public class CommonConfig {
}