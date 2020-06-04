// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.ExtensionsV1beta1Api;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1IngressList;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceList;
import lombok.extern.slf4j.Slf4j;

/**
 * An implementation of {@link ICanProvideInformers} that uses the Kubernetes {@link SharedInformerFactory}.
 */
@Service
@Slf4j
public class SharedIndexInformers implements ICanProvideInformers {
    private final SharedInformerFactory _informerFactory;
    private final SharedIndexInformer<ExtensionsV1beta1Ingress> _ingressInformer;
    private final SharedIndexInformer<V1Service> _serviceInformer;

    /**
     * Initializes a new instance of the {@link SharedIndexInformers} class.
     * @param apiClient The {@link ApiClient} used to interact with the Kubernetes Api Server.
     */
    @Autowired
    public SharedIndexInformers(ApiClient apiClient) {
        _informerFactory = new SharedInformerFactory(apiClient);

        var extensionsV1beta1Api = new ExtensionsV1beta1Api(apiClient);
        _ingressInformer = _informerFactory.sharedIndexInformerFor(
            (params) -> extensionsV1beta1Api.listIngressForAllNamespacesCall(null, null, null, null, null, null, params.resourceVersion, params.timeoutSeconds, params.watch, null),
            ExtensionsV1beta1Ingress.class,
            ExtensionsV1beta1IngressList.class);
        
        var coreV1Api = new CoreV1Api(apiClient);
        _serviceInformer = _informerFactory.sharedIndexInformerFor(
            (params) -> coreV1Api.listServiceForAllNamespacesCall(null, null, null, null, null, null, params.resourceVersion, params.timeoutSeconds, params.watch, null),
            V1Service.class,
            V1ServiceList.class);


        log.debug("Starting shared index informers");
        _informerFactory.startAllRegisteredInformers();
    }

    @Override
    public SharedInformerFactory getInformerFactory() {
        return _informerFactory;
    }
    
    @Override
    public SharedIndexInformer<ExtensionsV1beta1Ingress> getIngressInformer() {
        log.warn("GetIngressInformer");
        return _ingressInformer;
    }
    
    @Override
    public SharedIndexInformer<V1Service> getServiceInformer() {
        log.warn("GetServiceInformer");
        return _serviceInformer;
    }
}