// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.ingresses;

import io.dolittle.moose.kubernetes.Annotation;
import io.dolittle.moose.kubernetes.Namespace;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;

/**
 * Defines a system that can list instances of {@link ExtensionsV1beta1Ingress} in Kubernetes.
 */
public interface ICanListIngresses {
    /**
     * Gets all {@link ExtensionsV1beta1Ingress} in all namespaces.
     * @return An {@link Iterable} of type {@link ExtensionsV1beta1Ingress}.
     */
    Iterable<ExtensionsV1beta1Ingress> getAllIngresses();

    /**
     * Gets all {@link ExtensionsV1beta1Ingress} in all namespaces that matches a list of annotations.
     * @param annotations The list of {@link Annotation} to match.
     * @return An {@link Iterable} of type {@link ExtensionsV1beta1Ingress}.
     */
    Iterable<ExtensionsV1beta1Ingress> getAllIngressesWithAnnotations(Annotation... annotations);

    /**
     * Gets all {@link ExtensionsV1beta1Ingress} in a given namespace.
     * @param namespace The {@link Namespace} to get ingresses from.
     * @return An {@link Iterable} of type {@link ExtensionsV1beta1Ingress}.
     */
    Iterable<ExtensionsV1beta1Ingress> getIngressesInNamespace(Namespace namespace);
    
    /**
     * Gets all {@link ExtensionsV1beta1Ingress} in a given namespace that matches a list of annotations.
     * @param namespace The {@link Namespace} to get ingresses from.
     * @param annotations The list of {@link Annotation} to match.
     * @return An {@link Iterable} of type {@link ExtensionsV1beta1Ingress}.
     */
    Iterable<ExtensionsV1beta1Ingress> getIngressesInNamespaceWithAnnotations(Namespace namespace, Annotation... annotations);
}