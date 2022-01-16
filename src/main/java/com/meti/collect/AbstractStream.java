package com.meti.collect;

import com.meti.core.F1;
import com.meti.core.F2;
import com.meti.option.None;
import com.meti.option.Option;
import com.meti.option.Some;

public abstract class AbstractStream<T> implements Stream<T> {
    @Override
    public Stream<T> filter(F1<T, Boolean, ?> predicate) {
        return new AbstractStream<>() {
            @Override
            public T head() throws StreamException {
                try {
                    T next;
                    do {
                        next = AbstractStream.this.head();
                    } while (!predicate.apply(next));
                    return next;
                } catch (Exception e) {
                    throw new StreamException(e);
                }
            }
        };
    }

    @Override
    public Option<T> first() throws StreamException {
        try {
            return new Some<>(head());
        } catch (EndOfStreamException e) {
            return new None<>();
        }
    }

    @Override
    public <R, E extends Exception> R foldRight(R identity, F2<R, T, R, E> folder) throws StreamException, E {
        R current = identity;
        do {
            try {
                var next = head();
                current = folder.apply(current, next);
            } catch (EndOfStreamException e) {
                break;
            }
        } while (true);
        return current;
    }

    @Override
    public <R> Stream<R> map(F1<T, R, ?> mapper) throws StreamException {
        return new AbstractStream<>() {
            @Override
            public R head() throws StreamException {
                try {
                    return mapper.apply(AbstractStream.this.head());
                } catch (EndOfStreamException e) {
                    throw e;
                } catch (Exception e) {
                    throw new StreamException(e);
                }
            }
        };
    }
}
