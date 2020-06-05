// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import io.kubernetes.client.openapi.models.V1ObjectMeta;

/**
 * Represents a set of Annotations on a Kubernetes Resource
 */
public class Annotations {
    private final Map<String,Annotation> _entries;

    private Annotations(Map<String,Annotation> entries) {
        _entries = entries;
    }

    /**
     * Gets the entries of the set of annotations.
     * @return An {@link Iterable} of type {@link Annotation}.
     */
    public Iterable<Annotation> getEntries() {
        return _entries.values();
    }

    /**
     * Creates a new set of annotations by copying the current set, and adding the given annotations.
     * @param annotations The list of {@link Annotation} to add.
     * @return A new {@link Annotations}.
     * @throws AnnotationsAlreadyContainAnnotationWithKey If the current set of annotations already contain an {@link Annotation} with the same key as any of the given annotations.
     */
    public Annotations with(Annotation... annotations) throws AnnotationsAlreadyContainAnnotationWithKey {
        if (annotations.length == 0) {
            return this;
        }
        var map = new HashMap<>(_entries);
        for (var annotation : annotations) {
            if (map.containsKey(annotation.getKey())) {
                throw new AnnotationsAlreadyContainAnnotationWithKey(map.get(annotation.getKey()), annotation);
            }
            map.put(annotation.getKey(), annotation);
        }
        return new Annotations(map);
    }

    /**
     * Creates a new set of annotations by copying the current set, and adding a new annotation with the given key and value.
     * @param key The {@link String} to use as the {@link Annotation} key.
     * @param value The {@link String} to use as the {@link Annotation} value.
     * @return A new {@link Annotations}.
     * @throws AnnotationsAlreadyContainAnnotationWithKey If the current set of annotations already contain an {@link Annotation} with the given key.
     */
    public Annotations with(String key, String value) throws AnnotationsAlreadyContainAnnotationWithKey {
        return with(new Annotation(key, value));
    }

    /**
     * Creates an empty set of annotations.
     * @return An {@link Annotations}.
     */
    public static Annotations empty() {
        return new Annotations(new HashMap<>());
    }

    /**
     * Creates a set of annotations from a Kubernetes Metadata object.
     * @param meta The {@link V1ObjectMeta} containing the annotations to copy.
     * @return An {@link Annotations}.
     */
    public static Annotations from(V1ObjectMeta meta) {
        return new Annotations(meta.getAnnotations().entrySet().stream().collect(
            Collectors.toMap(
                (entry) -> entry.getKey(),
                (entry) -> new Annotation(entry.getKey(), entry.getValue()))));
    }
}