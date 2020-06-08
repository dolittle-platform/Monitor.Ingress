// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.ingresses;

import java.util.function.Predicate;

import org.springframework.stereotype.Component;

import io.dolittle.moose.kubernetes.Annotation;
import io.dolittle.moose.kubernetes.Namespace;

/**
 * An implementation of {@link ICanCreateIngressFilters}.
 */
@Component
public class FilterCreator implements ICanCreateIngressFilters {
    @Override
    public Predicate<Ingress> namespaceFilter(Namespace namespace) {
        return (ingress) -> ingress.getNamespace().equals(namespace);
    }

    @Override
    public Predicate<Ingress> annotationsFilter(Annotation... annotations) {
        return (ingress) -> ingress.getAnnotations().contains(annotations);
    }
}