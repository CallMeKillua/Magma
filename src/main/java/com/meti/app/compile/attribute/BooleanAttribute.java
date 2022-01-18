package com.meti.app.compile.attribute;

public record BooleanAttribute(boolean value) implements Attribute {
    @Override
    public boolean asBoolean() {
        return value;
    }
}