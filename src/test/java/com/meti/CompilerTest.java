package com.meti;

import org.junit.jupiter.api.Test;

import static com.meti.CompiledTests.assertCompile;

class CompilerTest {
    private static void assertInteger(int value) {
        var rendered = Compiler.renderInteger(value);
        assertCompile(rendered, rendered);
    }

    @Test
    void integer() {
        assertInteger(420);
    }

    @Test
    void another() {
        assertInteger(69);
    }
}