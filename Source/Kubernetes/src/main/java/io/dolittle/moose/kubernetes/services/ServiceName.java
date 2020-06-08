// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.services;

import lombok.Value;

/**
 * Represents the Name of a Kubernetes Service.
 */
@Value
public class ServiceName {
    String value;
}