// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.config;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import org.springframework.context.annotation.*;

import java.io.IOException;

@Configuration
@ComponentScan(basePackages = {"io.dolittle.moose.kubernetes"})
public class K8sConfig {
    @Bean
    public ApiClient getClient() throws IOException {
        return ClientBuilder.defaultClient();
    }
}
