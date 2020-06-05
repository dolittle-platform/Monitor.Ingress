// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes;

/**
 * Defines a wrapper type of a Kubernetes Resource definition that is scoped to a {@link Namespace}.
 */
public interface INamespacedResource {
    public Namespace getNamespace();
    public IName getName();
}