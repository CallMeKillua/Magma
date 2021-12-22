package com.meti.option;

import com.meti.core.F1;

public class None<T> implements Option<T> {
    @Override
    public <E extends Exception> Option<T> filter(F1<T, Boolean, E> filter) {
        return new None<>();
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public <R, E extends Exception> Option<R> map(F1<T, R, E> mapper) {
        return new None<>();
    }

    @Override
    public T orElse(T other) {
        return other;
    }
}