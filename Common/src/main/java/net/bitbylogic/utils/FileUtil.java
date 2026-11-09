package net.bitbylogic.utils;

import java.io.File;
import java.nio.file.Files;

public class FileUtil {

    public static void deleteDirectory(File directory) {
        if (directory == null) {
            return;
        }

        File[] contents = null;

        if (!Files.isSymbolicLink(directory.toPath())) {
            contents = directory.listFiles();
        }

        if (contents != null) {
            for (File file : contents) {
                deleteDirectory(file);
            }
        }

        directory.delete();
    }

}
