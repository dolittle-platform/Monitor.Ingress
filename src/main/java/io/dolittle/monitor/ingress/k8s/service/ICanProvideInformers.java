// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.monitor.ingress.k8s.service;

import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;
import io.kubernetes.client.openapi.models.V1Service;

/**
 * Defines a system that can provide instances of {@link SharedIndexInformer} for Kubernetes Resources.
 */
public interface ICanProvideInformers {
    /**
     * Gets the informer factory used to create the informers.
     * @return A {@link SharedInformerFactory}.
     */
    SharedInformerFactory getInformerFactory();

    /**
     * Gets an informer for Kubernetes Ingresses.
     * @return A {@link SharedIndexInformer} of type {@link ExtensionsV1beta1Ingress}.
     */
    SharedIndexInformer<ExtensionsV1beta1Ingress> getIngressInformer();

    /**
     * Gets an informer for Kubernets Services.
     * @return A {@link SharedIndexInformer} of type {@link V1Service}.
     */
    SharedIndexInformer<V1Service> getServiceInformer();
}