// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.ingresses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.dolittle.moose.kubernetes.Annotation;
import io.dolittle.moose.kubernetes.Namespace;
import io.dolittle.moose.kubernetes.eventhandlers.ListObservableEventHandler;
import io.dolittle.moose.kubernetes.informers.ICanProvideInformers;
import io.kubernetes.client.informer.SharedInformer;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;
import io.reactivex.rxjava3.core.Observable;

/**
 * An implementation of {@link ICanObserveIngresses} that uses {@link ICanProvideInformers} to get events about Ingresses.
 */
@Component
public class IngressObserver implements ICanObserveIngresses {
    private final SharedInformer<ExtensionsV1beta1Ingress> _informer;
    private final ICanCreateIngressFilters _filterCreator;

    /**
     * Initializes a new instance of the {@link IngressObserver} class.
     * @param informers The {@link ICanProvideInformers} to use to subscribe to Ingress events.
     * @param filterCreator The {@link ICanCreateIngressFilters} to use to create Ingress filters.
     */
    @Autowired
    public IngressObserver(ICanProvideInformers informers, ICanCreateIngressFilters filterCreator) {
        _informer = informers.getIngressInformer();
        _filterCreator = filterCreator;
    }

    @Override
    public Observable<Iterable<Ingress>> observeAllIngresses() {
        var handler = new ListObservableEventHandler<>(Ingress::from);
        _informer.addEventHandler(handler);
        return handler.getObservable();
    }
    
    @Override
    public Observable<Iterable<Ingress>> observeAllIngressesWithAnnotations(Annotation... annotations) {
        var filter = _filterCreator.annotationsFilter(annotations);
        var handler = new ListObservableEventHandler<>(Ingress::from, filter);
        _informer.addEventHandler(handler);
        return handler.getObservable();
    }
    
    @Override
    public Observable<Iterable<Ingress>> observeIngressesInNamespace(Namespace namespace) {
        var filter = _filterCreator.namespaceFilter(namespace);
        var handler = new ListObservableEventHandler<>(Ingress::from, filter);
        _informer.addEventHandler(handler);
        return handler.getObservable();
    }
    
    @Override
    public Observable<Iterable<Ingress>> observeIngressesInNamespaceWithAnnotations(Namespace namespace, Annotation... annotations) {
        var filter = _filterCreator.namespaceFilter(namespace).and(_filterCreator.annotationsFilter(annotations));
        var handler = new ListObservableEventHandler<>(Ingress::from, filter);
        _informer.addEventHandler(handler);
        return handler.getObservable();
    }
}