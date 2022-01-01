package com.meti;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationTest {
    private static final Path Root = Paths.get(".");
    private static final Path Target = Root.resolve("index.c");
    private static final Path Source = Root.resolve("index.mg");

    @Test
    void content() throws IOException {
        Files.writeString(Source, "def main() : I16 => {return 0;}");
        new Application(Source).run();
        assertEquals("int main(){return 0;}", Files.readString(Target));
    }

    @Test
    void creates_target() throws IOException {
        Files.createFile(Source);
        new Application(Source).run();
        assertTrue(Files.exists(Target));
    }

    @Test
    void does_not_create_target() throws IOException {
        new Application(Source).run();
        assertFalse(Files.exists(Target));
    }

    @Test
    void empty() throws IOException {
        Files.writeString(Source, "");
        new Application(Source).run();
        assertEquals("", Files.readString(Target));
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Source);
        Files.deleteIfExists(Target);
    }
}
