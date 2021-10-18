package com.meti;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompilerTest {
    @Test
    void name() {
        assertCompile("def empty() : Void => {}", new FunctionRenderer().render("empty", "void", "{}"));
    }

    private void assertCompile(String input, String expected) {
        var compiler = new Compiler(input);
        var output = compiler.compile();
        assertEquals(expected, output);
    }

    @Test
    void type() {
        assertCompile("def supplier() : U16 => {return 420;}", new FunctionRenderer().render("supplier", "unsigned int", "{return 420;}"));
    }

    @Test
    void body() {
        assertCompile("def supplier() : I16 => {return 420;}", new FunctionRenderer().render("supplier", "int", "{return 420;}"));
    }
}