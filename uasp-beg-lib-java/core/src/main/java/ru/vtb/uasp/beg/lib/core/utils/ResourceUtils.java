package ru.vtb.uasp.beg.lib.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ResourceUtils {
    public static final String CLASSPATH_PREFIX = "classpath:";
    public static final String FILE_PREFIX = "file:";

    public static String getResourceAsString(String resourceLocation, String... more) {
        Objects.requireNonNull(resourceLocation, "Resource location must not be null");
        String path = getPath(resourceLocation, more);
        if (path.startsWith(CLASSPATH_PREFIX)) {
            path = path.substring(CLASSPATH_PREFIX.length());
            return getStringFromStream(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
        } else {
            try {
                return getStringFromStream(Files.newInputStream(Paths.get(normalizeFileName(path)), StandardOpenOption.READ));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String normalizeFileName(String fileName) {
        Objects.requireNonNull(fileName, "FileName must not be null");
        if (fileName.startsWith(FILE_PREFIX)) {
            fileName = fileName.substring(FILE_PREFIX.length());
        }
        return fileName;
    }

    public static String getStringFromStream(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    public static boolean isResourcePathExists(String resourceLocation) {
        Objects.requireNonNull(resourceLocation, "Resource location must not be null");
        if (resourceLocation.startsWith(CLASSPATH_PREFIX)) {
            String path = resourceLocation.substring(CLASSPATH_PREFIX.length()) + "/";
            return Thread.currentThread().getContextClassLoader().getResource(path) != null;
        } else {
            return Files.exists(Paths.get(normalizeFileName(resourceLocation)));
        }
    }

    public static String getPath(String first, String... next) {
        if (first.startsWith(CLASSPATH_PREFIX)) {
            return String.join("/", first, String.join("/", next));
        }
        return Paths.get(normalizeFileName(first), next).toString();
    }
}
