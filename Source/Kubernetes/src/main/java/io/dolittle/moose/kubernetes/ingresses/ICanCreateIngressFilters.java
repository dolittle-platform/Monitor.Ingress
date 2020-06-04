package io.dolittle.moose.kubernetes.ingresses;

import java.util.function.Predicate;

import io.dolittle.moose.kubernetes.Annotation;
import io.dolittle.moose.kubernetes.Namespace;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;

public interface ICanCreateIngressFilters {
    Predicate<ExtensionsV1beta1Ingress> namespaceFilter(Namespace namespace);

    Predicate<ExtensionsV1beta1Ingress> annotationsFilter(Annotation... annotations);
}

