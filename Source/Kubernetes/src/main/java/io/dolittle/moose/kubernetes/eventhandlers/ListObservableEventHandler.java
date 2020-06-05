// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.eventhandlers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import io.dolittle.moose.kubernetes.INamespacedResource;
import io.kubernetes.client.informer.ResourceEventHandler;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

/**
 * A {@link ResourceEventHandler} that exposes an {@link Observable} of type {@link Iterable} of resources that is updated on every change event.
 * It also accepts an optional {@link Predicate} filter which determines what resources should be part of the {@link Iterable}.
 * @param <ApiType> The type of resources to be contained in the list.
 */
public class ListObservableEventHandler<ApiType, WrappedType extends INamespacedResource> implements ResourceEventHandler<ApiType> {
    private final BehaviorSubject<Iterable<WrappedType>> _subject;
    private final Function<ApiType, WrappedType> _mapper;
    private final Predicate<WrappedType> _filter;

    /**
     * Initializes a new instance of the {@link ListObservableEventHandler} class without a filter.
     * Every resource from the change events will be present in the list.
     * @param mapper A {@link Function} that converts an api type to a wrapped type.
     */
    public ListObservableEventHandler(Function<ApiType, WrappedType> mapper) {
        _subject = BehaviorSubject.createDefault(List.<WrappedType>of());
        _mapper = mapper;
        _filter = (obj) -> true;
    }

    /**
     * Initializes a new instance of the {@link ListObservableEventHandler} class with the given filter.
     * Only resources from the change events that matches the filter will be present in the list.
     * @param mapper A {@link Function} that converts an api type to a wrapped type.
     * @param filter The {@link Predicate} filter that determines wheter a resource should be in the list or not.
     */
    public ListObservableEventHandler(Function<ApiType, WrappedType> mapper, Predicate<WrappedType> filter) {
        _subject = BehaviorSubject.createDefault(List.<WrappedType>of());
        _mapper = mapper;
        _filter = filter;
    }

    @Override
    public void onAdd(ApiType obj) {
        WrappedType mapped = _mapper.apply(obj);
        if (_filter.test(mapped)) {
            emit(newListFromCurrentWith(mapped));
        }
    }

    @Override
    public void onUpdate(ApiType oldObj, ApiType newObj) {
        WrappedType oldMapped = _mapper.apply(oldObj);
        var oldMatches = _filter.test(oldMapped);
        WrappedType newMapped = _mapper.apply(newObj);
        var newMatches = _filter.test(newMapped);

        if (!oldMatches && !newMatches) {
            return;
        } else if (!oldMatches && newMatches) {
            emit(newListFromCurrentWith(newMapped));
        } else if (oldMatches && !newMatches) {
            emit(newListFromCurrentWithout(oldMapped));
        } else {
            emit(newListFromCurrentReplacing(oldMapped, newMapped));
        }
    }

    @Override
    public void onDelete(ApiType obj, boolean deletedFinalStateUnknown) {
        WrappedType mapped = _mapper.apply(obj);
        if (_filter.test(mapped)) {
            emit(newListFromCurrentWithout(mapped));
        }
    }
    
    /**
     * Gets the observable list of resources.
     * @return An {@link Observable} of type {@link Iterable}.
     */
    public Observable<Iterable<WrappedType>> getObservable() {
        return _subject;
    }

    private void emit(List<WrappedType> list) {
        _subject.onNext(list);
    }

    private List<WrappedType> newListFromCurrentWith(WrappedType obj) {
        var newList = new ArrayList<WrappedType>();
        for (WrappedType existing : _subject.getValue()) {
            newList.add(existing);
        }
        newList.add(obj);
        return newList;
    }

    private List<WrappedType> newListFromCurrentWithout(WrappedType obj) {
        var newList = new ArrayList<WrappedType>();
        for (WrappedType existing : _subject.getValue()) {
            if (!referenceEquals(existing, obj)) {
                newList.add(existing);
            }
        }
        return newList;
    }

    private List<WrappedType> newListFromCurrentReplacing(WrappedType current, WrappedType with) {
        var newList = new ArrayList<WrappedType>();
        for (WrappedType existing : _subject.getValue()) {
            if (referenceEquals(existing, current)) {
                newList.add(with);
            } else {
                newList.add(existing);
            }
        }
        return newList;
    }

    private boolean referenceEquals(WrappedType left, WrappedType right) {
        return left.getNamespace().equals(right.getNamespace()) && left.getName().getValue().equals(right.getName().getValue());
    }
}