package com.meti;

import java.util.function.Predicate;

record NativeString(String internalValue) {
    static NativeString from(String unwrap) {
        return new NativeString(unwrap);
    }

    Option<Index> firstIndexOfCharByPredicate(Predicate<Character> predicate) {
        return Iterators.fromRange(0, length()).zip(iter())
                .map(tuple -> predicate.test(tuple.right()) ? new Some<>(tuple.left()) : new None<Integer>())
                .flatMap(Iterators::fromOption)
                .head()
                .map(Index::new);
    }

    public String internalValue() {
        return internalValue;
    }

    public Option<Index> firstIndexOfChar(char c) {
        var value = internalValue.indexOf(c);
        return value == -1
                ? new None<>()
                : new Some<>(new Index(value));
    }

    public NativeString slice(Range range) {
        return new NativeString(internalValue.substring(range.start, range.end));
    }

    public NativeString strip() {
        return from(internalValue.strip());
    }

    public int length() {
        return internalValue.length();
    }

    public boolean startsWith(NativeString other) {
        return internalValue.startsWith(other.internalValue);
    }

    public Iterator<Character> iter() {
        return new IndexedIterator<>() {
            @Override
            protected int size() {
                return internalValue.length();
            }

            @Override
            protected Option<Character> apply(int counter) {
                return new Some<>(internalValue.charAt(counter));
            }
        };
    }

    public boolean equalsTo(NativeString other) {
        return length() == other.length() &&
               iter().zip(other.iter()).allMatch(tuple -> tuple.left() == tuple.right());
    }

    public NativeString sliceTo(Index index) {
        return new NativeString(internalValue.substring(0, index.value));
    }

    public Tuple2<NativeString, NativeString> split(Index index) {
        var left = internalValue.substring(0, index.value);
        var right = internalValue.substring(index.value);
        return new Tuple2<>(new NativeString(left), new NativeString(right));
    }

    public Option<Index> firstIndexAfterSlice(NativeString value) {
        var index = internalValue.indexOf(value.internalValue);
        if (index == -1) {
            return new Some<>(new Index(index + value.length()));
        } else {
            return new None<>();
        }
    }

    public NativeString sliceFrom(Index index) {
        return new NativeString(internalValue.substring(index.value));
    }

    record Range(int start, int end) {
    }

    final class Index {
        private final int value;

        Index(int value) {
            this.value = value;
        }

        public Option<Range> to(Index other) {
            if ((other.value - value) >= 0) {
                return new Some<>(new Range(value, other.value));
            } else {
                return new None<>();
            }
        }

        public Option<Range> rangeBetweenFirstChar(char c) {
            return firstIndexOfChar(c).map(value -> new Range(this.value, value.value));
        }
    }
}