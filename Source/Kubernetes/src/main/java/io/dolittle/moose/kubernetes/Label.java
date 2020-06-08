// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes;

import lombok.Value;

/**
 * Represents a Label on a Kubernetes Resource
 */
@Value
public class Label {
    String key;
    String value;
}