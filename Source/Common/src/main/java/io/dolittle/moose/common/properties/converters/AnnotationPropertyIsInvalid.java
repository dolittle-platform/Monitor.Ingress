// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.common.properties.converters;

/**
 * The exception that gets thrown when trying to convert an invalid Annotation specifier string from a property using the {@link AnnotationConverter}.
 */
public class AnnotationPropertyIsInvalid extends IllegalArgumentException {
    private static final long serialVersionUID = -5499474418037813544L;

    /**
     * Initializes a new instance of the {@link AnnotationPropertyIsInvalid} class.
     * @param reason A {@link String} describing the reason why the value is not valid.
     * @param value The {@link String} that was provided to the converter.
     */
    public AnnotationPropertyIsInvalid(String reason, String value) {
        super(String.format("Could not convert \"%s\" to an Annotation because %s. A valid string should be on the format \"key[:value]\" where a valid key is described here https://kubernetes.io/docs/concepts/overview/working-with-objects/annotations/#syntax-and-character-set, and the optional value can be any string.", value, reason));
    }
}