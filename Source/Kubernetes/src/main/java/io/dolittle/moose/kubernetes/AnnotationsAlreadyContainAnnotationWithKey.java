// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes;

/**
 * The exception that gets thrown when trying to add an {@link Annotation} to an {@link Annotation} that already contain a {@link Annotation} with the same key.
 */
public class AnnotationsAlreadyContainAnnotationWithKey extends Exception {
    private static final long serialVersionUID = 7427669858167894821L;

    /**
     * Initializes a new instance of the {@link AnnotationsAlreadyContainAnnotationWithKey} class.
     * @param existing The {@link Annotation} that already existed in the set.
     * @param added The {@link Annotation} that was attempted to add to the set.
     */
    public AnnotationsAlreadyContainAnnotationWithKey(Annotation existing, Annotation added)
    {
        super(String.format("Tried to add \"%s\":\"%s\" to Annotations that already contains \"%s\":\"%s\".", added.getKey(), added.getValue(), existing.getKey(), existing.getValue()));
    }
}