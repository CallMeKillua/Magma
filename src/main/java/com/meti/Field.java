package com.meti;

public record Field(String name, String type) implements Node {
    @Override
    public boolean is(Type type) {
        throw new UnsupportedOperationException();
    }
}