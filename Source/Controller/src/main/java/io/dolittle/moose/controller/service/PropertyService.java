// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.controller.service;


import io.dolittle.moose.controller.properties.ControllerProperties;
import io.dolittle.moose.controller.properties.IngressProperties;
import io.dolittle.moose.controller.properties.ServiceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PropertyService {

    private final ControllerProperties controllerProperties;
    private final IngressProperties ingressProperties;
    private final ServiceProperties serviceProperties;

    @Autowired
    public PropertyService(ControllerProperties controllerProperties, IngressProperties ingressProperties, ServiceProperties serviceProperties) {
        this.controllerProperties = controllerProperties;
        this.ingressProperties = ingressProperties;
        this.serviceProperties = serviceProperties;
    }

    public ControllerProperties controller() {
        return controllerProperties;
    }

    public IngressProperties ingress() {
        return ingressProperties;
    }

    public ServiceProperties service() {
        return serviceProperties;
    }
}
