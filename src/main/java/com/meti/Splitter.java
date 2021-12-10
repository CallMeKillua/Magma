package com.meti;

import java.util.ArrayList;
import java.util.List;

public class Splitter {
    private final String input;

    public Splitter(String input) {
        this.input = input;
    }

    List<String> split() {
        var lines = new ArrayList<String>();
        var buffer = new StringBuilder();
        var depth = 0;
        for (int i = 0; i < input.length(); i++) {
            var c = input.charAt(i);
            if (c == ';' && depth == 0) {
                lines.add(buffer.toString());
                buffer = new StringBuilder();
            } else if (c == '}' && depth == 1) {
                depth--;
                buffer.append(c);
                lines.add(buffer.toString());
                buffer = new StringBuilder();
            } else {
                if (c == '{') depth++;
                if (c == '}') depth--;
                buffer.append(c);
            }
        }

        lines.add(buffer.toString());
        lines.removeIf(String::isBlank);
        return lines;
    }
}
