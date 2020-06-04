package io.dolittle.moose.kubernetes.ingresses;

import java.util.function.Predicate;

import io.dolittle.moose.kubernetes.Annotation;
import io.dolittle.moose.kubernetes.Namespace;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;

public class FilterCreator implements ICanCreateIngressFilters {
    @Override
    public Predicate<ExtensionsV1beta1Ingress> namespaceFilter(Namespace namespace) {
        return (ingress) -> ingress.getMetadata().getNamespace().equals(namespace.getValue());
    }

    @Override
    public Predicate<ExtensionsV1beta1Ingress> annotationsFilter(Annotation... annotations) {
        return (ingress) -> {
            var ingressAnnotations = ingress.getMetadata().getAnnotations();
            for (var annotation : annotations) {
                if (!ingressAnnotations.containsKey(annotation.getKey())) {
                    return false;
                }
                if (!ingressAnnotations.get(annotation.getKey()).equals(annotation.getValue())) {
                    return false;
                }
            }
            return true;
        };
    }
}