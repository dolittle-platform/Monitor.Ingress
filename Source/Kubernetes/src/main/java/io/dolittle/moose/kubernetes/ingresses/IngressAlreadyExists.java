// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.ingresses;

import io.dolittle.moose.kubernetes.Namespace;
import io.dolittle.moose.kubernetes.errors.ResourceAlreadyExists;

/**
 * The exception that gets thrown when trying to create an {@link Ingress} in Kubernetes and an {@link Ingress} with the same {@link IngressName} in the same {@link Namespace} already exists.
 */
public class IngressAlreadyExists extends ResourceAlreadyExists {
    private static final long serialVersionUID = -6428167195296667526L;

    /**
     * Initializes a new instance of the {@link IngressAlreadyExists} class.
     * @param namespace The {@link Namespace} in which the Ingress was attempted to create.
     * @param name      The {@link IngressName} of the Ingress that was attempted to create.
     */
    public IngressAlreadyExists(Namespace namespace, IngressName name) {
        super("Ingress", namespace, name);
    }
}