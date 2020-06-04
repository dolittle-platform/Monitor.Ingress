// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.ingresses;

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
    private final Indexer<ExtensionsV1beta1Ingress> _indexer;
    private final ICanCreateIngressFilters _filterCreator;

    /**
     * Initializes a new instance of the {@link IngressLister} class.
     * @param informers The {@link ICanProvideInformers} to use to get Ingresses.
     * @param filterCreator The {@link ICanCreateIngressFilters} to use to create Ingress filters.
     */
    @Autowired
    public IngressLister(ICanProvideInformers informers, ICanCreateIngressFilters filterCreator) {
        _indexer = informers.getIngressInformer().getIndexer();
        _filterCreator = filterCreator;
    }

    @Override
    public Iterable<ExtensionsV1beta1Ingress> getAllIngresses() {
        return _indexer.list();
    }

    @Override
    public Iterable<ExtensionsV1beta1Ingress> getAllIngressesWithAnnotations(Annotation... annotations) {
        var filter = _filterCreator.annotationsFilter(annotations);
        return _indexer.list().stream().filter(filter)::iterator;
    }
    
    @Override
    public Iterable<ExtensionsV1beta1Ingress> getIngressesInNamespace(Namespace namespace) {
        var filter = _filterCreator.namespaceFilter(namespace);
        return _indexer.list().stream().filter(filter)::iterator;
    }
    
    @Override
    public Iterable<ExtensionsV1beta1Ingress> getIngressesInNamespaceWithAnnotations(Namespace namespace, Annotation... annotations) {
        var filter = _filterCreator.namespaceFilter(namespace).and(_filterCreator.annotationsFilter(annotations));
        return _indexer.list().stream().filter(filter)::iterator;
    }
}