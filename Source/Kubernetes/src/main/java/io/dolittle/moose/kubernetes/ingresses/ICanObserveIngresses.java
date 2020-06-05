// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.ingresses;

import io.dolittle.moose.kubernetes.Annotation;
import io.dolittle.moose.kubernetes.Namespace;
import io.reactivex.rxjava3.core.Observable;

/**
 * Defines a system that can observe instances of {@link Ingress} in Kubernetes.
 */
public interface ICanObserveIngresses {
    /**
     * Observe all {@link Ingress} in all namespaces.
     * @return An {@link Observable} of type {@link Iterable} of type {@link Ingress}.
     */
    Observable<Iterable<Ingress>> observeAllIngresses();

    /**
     * Observe all {@link Ingress} in all namespaces that matches a list of annotations.
     * @param annotations The list of {@link Annotation} to match.
     * @return An {@link Observable} of type {@link Iterable} of type {@link Ingress}.
     */
    Observable<Iterable<Ingress>> observeAllIngressesWithAnnotations(Annotation... annotations);

    /**
     * Observe all {@link Ingress} in a given namespace.
     * @param namespace The {@link Namespace} to get ingresses from.
     * @return An {@link Observable} of type {@link Iterable} of type {@link Ingress}.
     */
    Observable<Iterable<Ingress>> observeIngressesInNamespace(Namespace namespace);
    
    /**
     * Observe all {@link Ingress} in a given namespace that matches a list of annotations.
     * @param namespace The {@link Namespace} to get ingresses from.
     * @param annotations The list of {@link Annotation} to match.
     * @return An {@link Observable} of type {@link Iterable} of type {@link Ingress}.
     */
    Observable<Iterable<Ingress>> observeIngressesInNamespaceWithAnnotations(Namespace namespace, Annotation... annotations);
}