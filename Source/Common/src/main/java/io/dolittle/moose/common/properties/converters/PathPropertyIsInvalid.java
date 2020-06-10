// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.common.properties.converters;

/**
 * The exception that gets thrown when trying to convert an invalid Path specifier string from a property using the {@link PathConverter}.
 */
public class PathPropertyIsInvalid extends IllegalArgumentException {
    private static final long serialVersionUID = 6912291605328230343L;

    /**
     * Initializes a new instance of the {@link PathPropertyIsInvalid} class.
     * @param reason A {@link String} describing the reason why the value is not valid.
     * @param value  The {@link String} that was provided to the converter.
     */
    public PathPropertyIsInvalid(String reason, String value) {
        super(String.format("Could not convert \"%s\" to a Path because %s", value, reason));
    }
}