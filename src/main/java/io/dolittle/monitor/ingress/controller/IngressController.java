// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.monitor.ingress.controller;

import io.dolittle.monitor.ingress.k8s.service.ICanProvideInformers;
import io.kubernetes.client.extended.controller.Controller;
import io.kubernetes.client.extended.controller.builder.ControllerBuilder;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IngressController implements ApplicationRunner {

    private final ICanProvideInformers _informers;
    private final IngressReconciler _reconciler;

    @Autowired
    public IngressController(ICanProvideInformers informers, IngressReconciler reconciler) {
        _informers = informers;
        _reconciler = reconciler;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.debug("Running IngressController");

        var ingressInformer = _informers.getIngressInformer();

        Controller controller = ControllerBuilder.defaultBuilder(_informers.getInformerFactory())
                .watch((requestWorkQueue) -> ControllerBuilder.controllerWatchBuilder(ExtensionsV1beta1Ingress.class, requestWorkQueue).build())
                .withReconciler(_reconciler)
                .withName("uptime-ing-ctl")
                .withReadyFunc(ingressInformer::hasSynced)
                .build();

        controller.run();

        log.trace("Done");
    }
}
