// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.ingresses;

import org.springframework.beans.factory.annotation.Autowired;

import io.dolittle.moose.kubernetes.Annotation;
import io.dolittle.moose.kubernetes.Namespace;
import io.dolittle.moose.kubernetes.eventhandlers.ListObservableEventHandler;
import io.dolittle.moose.kubernetes.informers.ICanProvideInformers;
import io.kubernetes.client.informer.SharedInformer;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;
import io.reactivex.rxjava3.core.Observable;

public class IngressObserver implements ICanObserveIngresses {
    private final SharedInformer<ExtensionsV1beta1Ingress> _informer;

    @Autowired
    public IngressObserver(ICanProvideInformers informers) {
        _informer = informers.getIngressInformer();
    }

    @Override
    public Observable<Iterable<ExtensionsV1beta1Ingress>> observeAllIngresses() {
        var handler = new ListObservableEventHandler<ExtensionsV1beta1Ingress>();
        _informer.addEventHandler(handler);
        return handler.getObservable();
    }

    @Override
    public Observable<Iterable<ExtensionsV1beta1Ingress>> observeAllIngressesWithAnnotations(Annotation... annotations) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Observable<Iterable<ExtensionsV1beta1Ingress>> observeIngressesInNamespace(Namespace namespace) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Observable<Iterable<ExtensionsV1beta1Ingress>> observeIngressesInNamespaceWithAnnotations(Namespace namespace, Annotation... annotations) {
        // TODO Auto-generated method stub
        return null;
    }

}