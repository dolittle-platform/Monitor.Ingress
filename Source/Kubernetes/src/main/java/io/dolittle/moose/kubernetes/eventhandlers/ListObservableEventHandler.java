package io.dolittle.moose.kubernetes.eventhandlers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import io.kubernetes.client.informer.ResourceEventHandler;
import io.kubernetes.client.informer.cache.Caches;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class ListObservableEventHandler<ApiType> implements ResourceEventHandler<ApiType> {
    private final BehaviorSubject<Iterable<ApiType>> _subject;
    private final Predicate<ApiType> _filter;

    public ListObservableEventHandler() {
        _subject = BehaviorSubject.createDefault(List.<ApiType>of());
        _filter = (obj) -> true;
    }

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