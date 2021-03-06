// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.ingresses;

import io.dolittle.moose.kubernetes.IName;
import lombok.Value;

/**
 * Represents the Name of a Kubernetes Ingress
 */
@Value
public class IngressName implements IName {
    String value;
}