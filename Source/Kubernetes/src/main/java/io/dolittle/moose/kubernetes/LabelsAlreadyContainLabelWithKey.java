// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes;

/**
 * The exception that gets thrown when trying to add a {@link Label} to a {@link Labels} that already contain a {@link Label} with the same key.
 */
public class LabelsAlreadyContainLabelWithKey extends Exception {
    private static final long serialVersionUID = 7427669858167894821L;

    /**
     * Initializes a new instance of the {@link LabelsAlreadyContainLabelWithKey} class.
     * @param existing The {@link Label} that already existed in the set.
     * @param added The {@link Label} that was attempted to add to the set.
     */
    public LabelsAlreadyContainLabelWithKey(Label existing, Label added)
    {
        super(String.format("Tried to add \"%s\":\"%s\" to Labels that already contains \"%s\":\"%s\".", added.getKey(), added.getValue(), existing.getKey(), existing.getValue()));
    }
}