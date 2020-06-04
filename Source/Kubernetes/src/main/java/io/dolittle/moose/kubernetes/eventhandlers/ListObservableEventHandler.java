// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.eventhandlers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import io.kubernetes.client.informer.ResourceEventHandler;
import io.kubernetes.client.informer.cache.Caches;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

/**
 * A {@link ResourceEventHandler} that exposes an {@link Observable} of type {@link Iterable} of resources that is updated on every change event.
 * It also accepts an optional {@link Predicate} filter which determines what resources should be part of the {@link Iterable}.
 * @param <ApiType> The type of resources to be contained in the list.
 */
public class ListObservableEventHandler<ApiType> implements ResourceEventHandler<ApiType> {
    private final BehaviorSubject<Iterable<ApiType>> _subject;
    private final Predicate<ApiType> _filter;

    /**
     * Initializes a new instance of the {@link ListObservableEventHandler} class without a filter.
     * Every resource from the change events will be present in the list.
     */
    public ListObservableEventHandler() {
        _subject = BehaviorSubject.createDefault(List.<ApiType>of());
        _filter = (obj) -> true;
    }

    /**
     * Initializes a new instance of the {@link ListObservableEventHandler} class with the given filter.
     * Only resources from the change events that matches the filter will be present in the list.
     * @param filter The {@link Predicate} filter that determines wheter a resource should be in the list or not.
     */
    public ListObservableEventHandler(Predicate<ApiType> filter) {
        _subject = BehaviorSubject.createDefault(List.<ApiType>of());
        _filter = filter;
    }

    @Override
    public void onAdd(ApiType obj) {
        if (_filter.test(obj)) {
            emit(newListFromCurrentWith(obj));
        }
    }

    @Override
    public void onUpdate(ApiType oldObj, ApiType newObj) {
        var oldMatches = _filter.test(oldObj);
        var newMatches = _filter.test(newObj);

        if (!oldMatches && !newMatches) {
            return;
        } else if (!oldMatches && newMatches) {
            emit(newListFromCurrentWith(newObj));
        } else if (oldMatches && !newMatches) {
            emit(newListFromCurrentWithout(oldObj));
        } else {
            emit(newListFromCurrentReplacing(oldObj, newObj));
        }
    }

    @Override
    public void onDelete(ApiType obj, boolean deletedFinalStateUnknown) {
        if (_filter.test(obj)) {
            emit(newListFromCurrentWithout(obj));
        }
    }
    
    /**
     * Gets the observable list of resources.
     * @return An {@link Observable} of type {@link Iterable}.
     */
    public Observable<Iterable<ApiType>> getObservable() {
        return _subject;
    }

    private void emit(List<ApiType> list) {
        _subject.onNext(list);
    }

    private List<ApiType> newListFromCurrentWith(ApiType obj) {
        var newList = new ArrayList<ApiType>();
        for (var existing : _subject.getValue()) {
            newList.add(existing);
        }
        newList.add(obj);
        return newList;
    }

    private List<ApiType> newListFromCurrentWithout(ApiType obj) {
        var newList = new ArrayList<ApiType>();
        var keyToRemove = getKeyFor(obj);
        for (var existing : _subject.getValue()) {
            if (!keyToRemove.equals(getKeyFor(existing))) {
                newList.add(existing);
            }
        }
        return newList;
    }

    private List<ApiType> newListFromCurrentReplacing(ApiType current, ApiType with) {
        var newList = new ArrayList<ApiType>();
        var keyToReplace = getKeyFor(current);
        for (var existing : _subject.getValue()) {
            if (keyToReplace.equals(getKeyFor(existing))) {
                newList.add(with);
            } else {
                newList.add(existing);
            }
        }
        return newList;
    }

    private String getKeyFor(ApiType obj) {
        return Caches.deletionHandlingMetaNamespaceKeyFunc(obj);
    }
}