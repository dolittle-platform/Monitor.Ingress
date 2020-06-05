// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.ingresses;

import io.dolittle.moose.kubernetes.Namespace;

public interface IIngressApiClient {
    /**
     * Checks whether or not an Ingress with the given Namespace and Name exists in Kubernetes.
     * @param namespace The {@link Namespace} to look in.
     * @param name The {@link IngressName} name to look for.
     * @return {@literal true} if an Ingress with the given Namespace and Name exists, {@literal false} if not.
     */
    boolean Exists(Namespace namespace, IngressName name);

    /**
     * Checks whether or not an Ingress with the same Namespace and Name as the given {@link Ingress} exists in Kubernetes.
     * @param ingress The {@link Ingress} which {@link Namespace} and {@link IngressName} to look for.
     * @return {@literal true} if an Ingress with the given Namespace and Name exists, {@literal false} if not.
     */
    boolean Exists(Ingress ingress);

    /**
     * Gets an Ingress with the given Namespace and Name from Kubernetes.
     * @param namespace The {@link Namespace} to get from.
     * @param name The {@link IngressName} to get.
     * @return An {@link Ingress}.
     * @throws IngressDoesNotExist If the Ingress with the given Name in the given Namespace does not exist.
     */
    Ingress Get(Namespace namespace, IngressName name) throws IngressDoesNotExist;

    /**
     * Gets an Ingress in the same Namespace and Name as the given {@link Ingress} form Kubernetes.
     * @param ingress The {@link Ingress} which {@link Namespace} and {@link IngressName} to get.
     * @return An {@link Ingress}.
     * @throws IngressDoesNotExist If the Ingress with the given Name in the given Namespace does not exist.
     */
    Ingress Get(Ingress ingress) throws IngressDoesNotExist;

    /**
     * Create an Ingress as described by the provider {@link Ingress} in Kubernetes.
     * @param ingress The {@link Ingress} that describes the Ingress to create.
     * @throws IngressAlreadyExists If an Ingress with the same Name and Namespace as the given {@link Ingress} already exists.
     */
    void Create(Ingress ingress) throws IngressAlreadyExists;

    /**
     * Updates an Ingress to match the description of the provided {@link Ingress} in Kubernetes.
     * @param ingress The {@link Ingress} that describes the desired state of an Ingress.
     * @throws IngressDoesNotExist  If the Ingress with the given Name in the given Namespace does not exist. 
     */
    void Update(Ingress ingress) throws IngressDoesNotExist;
}