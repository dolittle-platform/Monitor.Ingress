// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.errors;

import io.dolittle.moose.kubernetes.IName;
import io.dolittle.moose.kubernetes.Namespace;

/**
 * The exception that gets thrown when trying to get a Resource from Kubernetes
 * that does not exist.
 */
public abstract class ResourceDoesNotExist extends Exception {
    private static final long serialVersionUID = -7902496222007364568L;
    
    /**
     * Initializes a new instance of the {@link ResourceDoesNotExist} class for a Namespace scoped Resource.
     * @param kind The {@link String} Kind of the resource.
     * @param namespace The {@link Namespace} that was searched.
     * @param name The {@link IName} that was searched for.
     */
    protected ResourceDoesNotExist(String kind, Namespace namespace, IName name){
        super(String.format("The %s %s/%s does not exist.", kind, namespace.getValue(), name.getValue()));
    }

    /**
     * Initializes a new instance of the {@link ResourceDoesNotExist} class for a Cluster scoped Resource.
     * @param kind The {@link String} Kind of the resource.
     * @param name The {@link IName} that was searched for.
     */
    protected ResourceDoesNotExist(String kind, IName name) {
        super(String.format("The %s %s does not exist.", kind, name));
    }
}