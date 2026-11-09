package net.bitbylogic.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;

class FileUtilTest {

    @TempDir
    Path directory;

    @Test
    void nestedDirectoriesAreDeleted() throws Exception {
        Path nested = Files.createDirectories(directory.resolve("a/b"));
        Files.writeString(nested.resolve("file.txt"), "x");

        FileUtil.deleteDirectory(directory.resolve("a").toFile());

        assertFalse(Files.exists(directory.resolve("a")));
    }

    @Test
    void nullIsIgnored() {
        FileUtil.deleteDirectory((File) null);
    }

}
