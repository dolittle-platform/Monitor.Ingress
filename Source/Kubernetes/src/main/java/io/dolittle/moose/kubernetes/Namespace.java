// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes;

import lombok.Value;

/**
 * Represents a Namespace in Kubernetes
 */
@Value
public class Namespace {
    String value;
}