// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.services;

import io.kubernetes.client.custom.IntOrString;

/**
 * Represents the port number of a Kubernetes Service.
 */
public class Port extends IntOrString {
    /**
     * Initializes a new instance of the {@link Port} class with a reference to a named port.
     * @param value The {@link String} that references a named Kubernetes Service port.
     */
    public Port(String value) {
        super(value);
    }

    /**
     * Initializes a new instance of the {@link Port} class with a reference to a port number.
     * @param value The port number.
     */
    public Port(int value) {
        super(value);
    }

    /**
     * Creates a new {@link Port} from an {@link IntOrString} instance.
     * @param value The {@link IntOrString} to use to create the {@link Port}.
     * @return A new {@link Port}.
     */
    public static Port from(IntOrString value) {
        if (value.isInteger()) {
            return new Port(value.getIntValue());
        } else {
            return new Port(value.getStrValue());
        }
    }
}