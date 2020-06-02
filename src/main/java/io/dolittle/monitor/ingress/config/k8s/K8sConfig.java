// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.monitor.ingress.config.k8s;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.*;

import java.io.IOException;

@Configuration
@ComponentScan(basePackages = {"io.dolittle.monitor.ingress.k8s"})
@Slf4j
public class K8sConfig {

    @Profile("dev")
    @Bean
    public ApiClient getClientDev() throws IOException {
        return Config.fromConfig("config");
    }

    @Profile("prod")
    @Bean
    public ApiClient getClient() throws IOException {

        return ClientBuilder.cluster().build();

    }
}
