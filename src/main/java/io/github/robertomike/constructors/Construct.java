package io.github.robertomike.constructors;

import lombok.Getter;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Construct<T> extends AbstractList<T> {

    protected List<T> items = new ArrayList<>();

    @Override
    public boolean add(T t) {
        return items.add(t);
    }

    public void set(T item) {
        items.clear();
        items.add(item);
    }

    @SafeVarargs
    public final void addAll(T... item) {
        items.addAll(List.of(item));
    }

    @Override
    public T get(int index) {
        return items.get(index);
    }

    @Override
    public int size() {
        return items.size();
    }
}
