// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes;

/**
 * Defines a wrapper type of a Kubernetes Resource definition that is scoped to the Cluster.
 */
public interface IClusterResource {
    public IName getName();
}