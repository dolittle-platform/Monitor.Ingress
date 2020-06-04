// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.ingresses;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.dolittle.moose.kubernetes.Annotation;
import io.dolittle.moose.kubernetes.Namespace;
import io.dolittle.moose.kubernetes.informers.ICanProvideInformers;
import io.kubernetes.client.informer.cache.Indexer;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;

/**
 * An implementation of {@link ICanListIngresses} that uses {@link ICanProvideInformers} to get Ingresses.
 */
@Component
public class IngressLister implements ICanListIngresses {
    public final Indexer<ExtensionsV1beta1Ingress> _indexer;

    @Autowired
    public IngressLister(ICanProvideInformers informers) {
        _indexer = informers.getIngressInformer().getIndexer();
    }

    @Override
    public Iterable<ExtensionsV1beta1Ingress> getAllIngresses() {
        return _indexer.list();
    }

    @Override
    public Iterable<ExtensionsV1beta1Ingress> getAllIngressesWithAnnotations(Annotation... annotations) {
        return filterByAnnotations(_indexer.list().stream(), annotations)::iterator;
    }

    @Override
    public Iterable<ExtensionsV1beta1Ingress> getIngressesInNamespace(Namespace namespace) {
        return filterByNamespace(_indexer.list().stream(), namespace)::iterator;
    }

    @Override
    public Iterable<ExtensionsV1beta1Ingress> getIngressesInNamespaceWithAnnotations(Namespace namespace, Annotation... annotations) {
        return filterByAnnotations(filterByNamespace(_indexer.list().stream(), namespace), annotations)::iterator;
    }

    private Stream<ExtensionsV1beta1Ingress> filterByNamespace(Stream<ExtensionsV1beta1Ingress> ingresses, Namespace namespace) {
        return ingresses.filter((ingress) -> ingress.getMetadata().getNamespace().equals(namespace.getValue()));
    }

    private Stream<ExtensionsV1beta1Ingress> filterByAnnotations(Stream<ExtensionsV1beta1Ingress> ingresses, Annotation... annotations) {
        return ingresses.filter((ingress) -> {
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
        });
    }
}