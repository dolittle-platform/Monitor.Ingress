// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import io.kubernetes.client.openapi.models.V1ObjectMeta;

/**
 * Represents a set of Labels on a Kubernetes Resource
 */
public class Labels {
    private final Map<String,Label> _entries;

    private Labels(Map<String,Label> entries) {
        _entries = entries;
    }

    /**
     * Gets the entries of the set of labels.
     * @return An {@link Iterable} of type {@link Label}.
     */
    public Iterable<Label> getEntries() {
        return _entries.values();
    }

    /**
     * Creates a new set of labels by copying the current set, and adding the given labels.
     * @param labels The list of {@link Label} to add.
     * @return A new {@link Labels}.
     * @throws LabelsAlreadyContainLabelWithKey If the current set of labels already contain a {@link Label} with the same key as any of the given labels.
     */
    public Labels with(Label... labels) throws LabelsAlreadyContainLabelWithKey {
        if (labels.length == 0) {
            return this;
        }
        var map = new HashMap<>(_entries);
        for (var label : labels) {
            if (map.containsKey(label.getKey())) {
                throw new LabelsAlreadyContainLabelWithKey(map.get(label.getKey()), label);
            }
            map.put(label.getKey(), label);
        }
        return new Labels(map);
    }

    /**
     * Creates a new set of labels by copying the current set, and adding a new label with the given key and value.
     * @param key The {@link String} to use as the {@link Label} key.
     * @param value The {@link String} to use as the {@link Label} value.
     * @return A new {@link Labels}.
     * @throws LabelsAlreadyContainLabelWithKey If the current set of labels already contain a {@link Label} with the given key.
     */
    public Labels with(String key, String value) throws LabelsAlreadyContainLabelWithKey {
        return with(new Label(key, value));
    }

    /**
     * Creates an empty set of labels.
     * @return A {@link Labels}.
     */
    public static Labels empty() {
        return new Labels(new HashMap<>());
    }

    /**
     * Creates a set of labels from a Kubernetes Metadata object.
     * @param meta The {@link V1ObjectMeta} containing the labels to copy.
     * @return A {@link Labels}.
     */
    public static Labels from(V1ObjectMeta meta) {
        var map = meta.getLabels().entrySet().stream().collect(Collectors.toMap((entry) -> entry.getKey(), (entry) -> new Label(entry.getKey(), entry.getValue())));
        return new Labels(map);
    }
}