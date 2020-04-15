package com.skaggsm;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Objects;

/**
 * Utilities for extracting things on the classpath.
 */
public class ClasspathUtils {
    public static Path extractResourcesToTempDirectory(String source, String prefix, ClassLoader loader) throws IOException {
        Path tempFile = Files.createTempDirectory(prefix);
        deleteOnExit(tempFile);
        extractResources(source, tempFile, loader);
        return tempFile;
    }

    public static Path extractResourcesToTempDirectory(String source, String prefix, Class<?> klass) throws IOException {
        return extractResourcesToTempDirectory(source, prefix, klass.getClassLoader());
    }

    public static void extractResources(String source, Path targetPath, Class<?> klass) throws IOException {
        extractResources(source, targetPath, klass.getClassLoader());
    }

    public static void extractResources(String source, Path targetPath, ClassLoader loader) throws IOException {
        URL resource = loader.getResource(source);
        Objects.requireNonNull(resource, String.format("Resource %s was not found in ClassLoader %s", source, loader));

        try {
            visitUriAsPath(
                    resource.toURI(),
                    sourcePath -> Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            Path newPath = targetPath.resolve(sourcePath.getParent().relativize(file).toString());
                            Files.copy(file, newPath);
                            deleteOnExit(newPath);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                            Path newPath = targetPath.resolve(sourcePath.getParent().relativize(dir).toString());
                            Files.copy(dir, newPath);
                            deleteOnExit(newPath);
                            return FileVisitResult.CONTINUE;
                        }
                    })
            );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteOnExit(Path path) {
        path.toFile().deleteOnExit();
    }

    public static void visitUriAsPath(URI uri, PathConsumer<? extends IOException> visitor) throws IOException {
        try {
            Path p = Paths.get(uri);
            visitor.accept(p);
        } catch (FileSystemNotFoundException e) {
            try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                Path p = fs.provider().getPath(uri);
                visitor.accept(p);
            }
        }
    }

    @FunctionalInterface
    public interface PathConsumer<T extends Throwable> {
        void accept(Path path) throws T;
    }
}
