package com.meti;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NIOPathTest {
    private static final NIOPath Parent = NIOPath.Root.resolveChild("parent");

    @Test
    void computeFileNameWithoutExtension() {
    }

    @Test
    void createAsDirectory() {
    }

    @Test
    void createAsFile() {
    }

    @Test
    void delete() {
    }

    @Test
    void deleteAsDirectory() {
    }

    @Test
    void exists() {
    }

    @Test
    void hasExtensionOf() {
        var path = NIOPath.Root.resolveChild("test.ing");
        assertTrue(path.hasExtensionOf("ing"));
    }

    @Test
    void list() {
    }

    @Test
    void resolveChild() {
    }

    @Test
    void resolveSibling() {
    }

    @AfterEach
    void tearDown() throws IOException {
        Parent.deleteAsDirectory();
    }

    @Test
    void value() {
    }

    @Test
    void walk() throws IOException {
        Parent.createAsDirectory();

        var child = Parent.resolveChild("child");
        child.createAsDirectory();

        var grandChild = child.resolveChild("grandChild");
        grandChild.createAsFile();

        var expected = List.of(Parent, child, grandChild);
        var actual = Parent.walk().collect(Collectors.toList());
        assertIterableEquals(expected, actual);
    }
}