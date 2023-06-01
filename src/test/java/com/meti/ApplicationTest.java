package com.meti;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.meti.Option.Some;
import static org.junit.jupiter.api.Assertions.*;

public class ApplicationTest {

    private Path target;
    private Path source;

    @BeforeEach
    void setUp() {
        target = Paths.get(".", "Main.mgs");
        source = Paths.get(".", "Main.java");
    }

    @Test
    void generatesProperTarget() throws IOException {
        Files.createFile(source);
        var actual = run(source);
        assertEquals(target, actual.unwrap());
    }

    @Test
    void generatesTarget() throws IOException {
        Files.createFile(source);
        run(source);
        assertTrue(Files.exists(target));
    }

    private Option<Path> run(Path source) throws IOException {
        if (Files.exists(source)) {
            var name = source.getFileName().toString();
            var separator = name.indexOf('.');
            var nameWithoutExtension = name.substring(0, separator);
            var target = source.resolveSibling(nameWithoutExtension + ".mgs");
            Files.createFile(target);
            return Some(target);
        } else {
            return Option.None();
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(target);
        Files.deleteIfExists(source);
    }

    @Test
    void generatesNothing() {
        assertFalse(Files.exists(target));
    }
}
