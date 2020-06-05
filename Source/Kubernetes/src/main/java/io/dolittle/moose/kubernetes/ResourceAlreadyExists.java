// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes;

/**
 * The exception that gets thrown when trying to create a Resource in Kubernetes and a Resource with the same Name (in the same Namespace) already exists.
 */
public abstract class ResourceAlreadyExists extends Exception {
    private static final long serialVersionUID = -6428167195296667526L;

    /**
     * Initializes a new instance of the {@link ResourceAlreadyExists} class for a Namespace scoped Resource.
     * @param kind      The {@link String} Kind of the resource.
     * @param namespace The {@link Namespace} in which the Resource was attempted to create.
     * @param name      The {@link IName} of the Resource that was attempted to create.
     */
    protected ResourceAlreadyExists(String kind, Namespace namespace, IName name){
        super(String.format("The %s %s/%s already exists.", kind, namespace.getValue(), name.getValue()));
    }

    /**
     * Initializes a new instance of the {@link ResourceAlreadyExists} class for a Cluster scoped Resource.
     * @param kind The {@link String} Kind of the resource.
     * @param name      The {@link IName} of the Resource that was attempted to create.
     */
    protected ResourceAlreadyExists(String kind, IName name) {
        super(String.format("The %s %s already exists.", kind, name));
    }
}