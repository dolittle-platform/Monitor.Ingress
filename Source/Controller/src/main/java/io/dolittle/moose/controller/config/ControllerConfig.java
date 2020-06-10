// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.controller.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Profile("controller")
@Configuration
@ComponentScan(basePackages = {"io.dolittle.moose.controller", "io.dolittle.moose.common.config"})
@ConfigurationPropertiesScan(basePackages = {"io.dolittle.moose.controller"})
@PropertySource(value = {"classpath:controller.properties"})
public class ControllerConfig {

    // @Bean
    // public SharedInformerFactory UptimeSharedInformerFactory() {
    //     // instantiating an informer-factory, and there should be only one informer-factory globally.
    //     return new SharedInformerFactory();
    // }

    // @Bean
    // public SharedIndexInformer<ExtensionsV1beta1Ingress> ingressInformer(SharedInformerFactory informerFactory, KubernetesService kubernetesService) {
    //     return informerFactory.sharedIndexInformerFor((CallGeneratorParams params) -> {
    //                 log.info("params.watch: {}, version: {}, seconds: {}", params.watch, params.resourceVersion, params.timeoutSeconds);
    //                 return kubernetesService.getExtV1beta1Api().listIngressForAllNamespacesCall(null, null,
    //                         null, null, null, null,
    //                         params.resourceVersion, params.timeoutSeconds,
    //                         params.watch, null);
    //             },
    //             ExtensionsV1beta1Ingress.class, ExtensionsV1beta1IngressList.class);
    // }
}
