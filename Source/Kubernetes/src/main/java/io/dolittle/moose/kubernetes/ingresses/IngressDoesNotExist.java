// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.ingresses;

import io.dolittle.moose.kubernetes.Namespace;
import io.dolittle.moose.kubernetes.ResourceDoesNotExist;

/**
 * The exception that gets thrown when trying to get an {@link Ingress} from
 * Kubernetes that does not exist.
 */
public class IngressDoesNotExist extends ResourceDoesNotExist {
    private static final long serialVersionUID = -2835691782847282883L;

    /**
     * Initializes a new instance of the {@link IngressDoesNotExist} class.
     * @param namespace The {@link Namespace} that was searched.
     * @param name The {@link IngressName} that was searched for.
     */
    public IngressDoesNotExist(Namespace namespace, IngressName name){
        super("Ingress", namespace, name);
    }
}