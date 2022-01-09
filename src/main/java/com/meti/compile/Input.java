package com.meti.compile;

public class Input {
    private final String input;

    public Input(String input) {
        this.input = input.trim();
    }

    boolean endsWithChar() {
        return hasContent() && getInput().indexOf('}') == input.length() - 1;
    }

    private boolean hasContent() {
        return getInput().length() != 0;
    }

    public String getInput() {
        return input;
    }

    public boolean isEmpty() {
        return input.length() == 0;
    }

    Input slice(int start, int end) {
        return new Input(getInput().substring(start, end));
    }

    public Input slice(int offset) {
        return new Input(input.substring(offset));
    }

    boolean startsWithChar() {
        return hasContent() && getInput().indexOf('{') == 0;
    }
}
