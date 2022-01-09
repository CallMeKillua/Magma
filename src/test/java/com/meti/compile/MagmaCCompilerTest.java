package com.meti.compile;

import org.junit.jupiter.api.Test;

import static com.meti.compile.CompiledTest.assertCompile;

class MagmaCCompilerTest {
    @Test
    void compile() {
        assertCompile("def test() : I16 => {return 0;}", "int test(){return 0;}");
    }
}