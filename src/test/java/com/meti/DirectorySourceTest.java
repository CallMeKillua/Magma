package com.meti;

import com.meti.source.DirectorySource;
import com.meti.stream.StreamException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DirectorySourceTest {
    public static final Path Root = Paths.get(".", "gen_test");
    public static final Path Child = Root.resolve("child");
    public static final Path First = Root.resolve("first.mgs");
    public static final Path Second = Child.resolve("second.mgs");

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectory(Root);
        Files.createDirectory(Child);

        Files.createFile(First);
        Files.createFile(Second);
    }

    @Test
    void stream() throws IOException, StreamException {
        var source = new DirectorySource(Root);
        var scripts = source.stream().map(Script::asString)
                .foldRight(new HashSet<String>(), (current, next) -> {
                    var copy = new HashSet<>(current);
                    copy.add(next);
                    return copy;
                });

        assertTrue(scripts.contains(First.toAbsolutePath().toString()));
        assertTrue(scripts.contains(Second.toAbsolutePath().toString()));
        assertFalse(scripts.contains(Child.toAbsolutePath().toString()));
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walkFileTree(Root, new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}