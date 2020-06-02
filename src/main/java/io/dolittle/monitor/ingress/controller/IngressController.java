// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.monitor.ingress.controller;

import io.kubernetes.client.extended.controller.Controller;
import io.kubernetes.client.extended.controller.builder.ControllerBuilder;
import io.kubernetes.client.extended.controller.reconciler.Reconciler;
import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IngressController implements ApplicationRunner {

    private final SharedInformerFactory informerFactory;
    private final SharedIndexInformer<ExtensionsV1beta1Ingress> ingressInformer;
    private final Reconciler ingressReconciler;

    @Autowired
    public IngressController( SharedInformerFactory informerFactory, SharedIndexInformer<ExtensionsV1beta1Ingress> ingressInformer, IngressReconciler ingressReconciler) {
        this.informerFactory = informerFactory;
        this.ingressInformer = ingressInformer;
        this.ingressReconciler = ingressReconciler;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Running IngressController");

        informerFactory.startAllRegisteredInformers();

        Controller controller = ControllerBuilder.defaultBuilder(informerFactory)
                .watch((requestWorkQueue) -> ControllerBuilder.controllerWatchBuilder(ExtensionsV1beta1Ingress.class, requestWorkQueue)
                        .build())
                .withReconciler(ingressReconciler)
                .withName("uptime-ing-ctl")
                .withReadyFunc(ingressInformer::hasSynced)
                .build();

        controller.run();

        log.info("Done");

    }
}
