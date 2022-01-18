package com.meti.app.compile.node;

import com.meti.api.option.None;
import com.meti.api.option.Option;
import com.meti.api.option.Some;

import java.util.Objects;

public final class Text {
    public final String trimmedValue;
    private final String value;

    public Text(String value) {
        this.value = value;
        trimmedValue = value.trim();
    }

    public Text append(String slice) {
        return new Text(trimmedValue + slice);
    }

    public Text append(Text other) {
        return new Text(trimmedValue + other.trimmedValue);
    }

    public Text appendRaw(String suffix) {
        return new Text(value + suffix);
    }

    public char apply(int index) {
        return trimmedValue.charAt(index);
    }

    public String compute() {
        return value;
    }

    public String computeTrimmed() {
        return trimmedValue;
    }

    public boolean containsChar(char c) {
        return trimmedValue.indexOf(c) != -1;
    }

    public boolean endsWithChar(char c) {
        var trimmed = trimmedValue;
        return trimmed.length() != 0 && trimmed.charAt(trimmed.length() - 1) == c;
    }

    public Option<Integer> firstIndexOfChar(char c) {
        var index = trimmedValue.indexOf(c);
        return index == -1
                ? new None<>()
                : new Some<>(index);
    }

    public Option<Integer> firstIndexOfCharWithOffset(char c, int offset) {
        var index = trimmedValue.indexOf(c, offset);
        return index == -1
                ? new None<>()
                : new Some<>(index);
    }

    public Option<Integer> firstIndexOfSlice(String slice) {
        var index = trimmedValue.indexOf(slice);
        return index == -1
                ? new None<>()
                : new Some<>(index);
    }

    public Option<Integer> firstIndexOfSlice(String slice, int offset) {
        var index = trimmedValue.indexOf(slice, offset);
        return index == -1
                ? new None<>()
                : new Some<>(index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trimmedValue, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Text text)) return false;
        return Objects.equals(trimmedValue, text.trimmedValue);
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }

    public boolean isEmpty() {
        return trimmedValue.length() == 0;
    }

    public Option<Integer> lastIndexOfChar(char c) {
        var index = trimmedValue.lastIndexOf(c);
        return index == -1
                ? new None<>()
                : new Some<>(index);
    }

    public Text prepend(String prefix) {
        return new Text(prefix + trimmedValue);
    }

    public Text prependRaw(String prefix) {
        return new Text(prefix + value);
    }

    public int size() {
        return trimmedValue.length();
    }

    public Text slice(int start, int end) {
        return new Text(trimmedValue.substring(start, end));
    }

    public Text slice(int offset) {
        return new Text(trimmedValue.substring(offset));
    }

    public Text sliceRaw(int start, int end) {
        return new Text(value.substring(start, end));
    }

    public boolean startsWithChar(char c) {
        return hasContent() && trimmedValue.indexOf(c) == 0;
    }

    private boolean hasContent() {
        return trimmedValue.length() != 0;
    }

    public boolean startsWithSlice(String slice) {
        return trimmedValue.startsWith(slice);
    }
}