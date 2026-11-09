package net.bitbylogic.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

public class FileUtil {

    private static final Logger LOGGER = Logger.getLogger(FileUtil.class.getName());

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

        try {
            Files.deleteIfExists(directory.toPath());
        } catch (IOException e) {
            LOGGER.warning("Failed to delete " + directory + ": " + e.getMessage());
        }
    }

}
