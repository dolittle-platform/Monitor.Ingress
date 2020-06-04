package io.dolittle.moose.kubernetes.ingresses;

import java.util.function.Predicate;

import io.dolittle.moose.kubernetes.Annotation;
import io.dolittle.moose.kubernetes.Namespace;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;

/**
 * Defines a system that can create {@link Predicate} filters for {@link ExtensionsV1beta1Ingress}.
 */
public interface ICanCreateIngressFilters {
    /**
     * Creates a filter that matches only ingresses in the given namespace.
     * @param namespace The {@link Namespace} to match against.
     * @return A {@link Predicate} of type {@link ExtensionsV1beta1Ingress}
     */
    Predicate<ExtensionsV1beta1Ingress> namespaceFilter(Namespace namespace);

    /**
     * Creates a filter that matches only ingresses that contain all of the given annotations.
     * @param annotations The list of {@link Annotation} to match against.
     * @return A {@link Predicate} of type {@link ExtensionsV1beta1Ingress}
     */
    Predicate<ExtensionsV1beta1Ingress> annotationsFilter(Annotation... annotations);
}

