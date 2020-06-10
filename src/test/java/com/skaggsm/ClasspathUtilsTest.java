package com.skaggsm;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Mitchell Skaggs on 4/15/2020.
 */
class ClasspathUtilsTest {
    @Test
    void given_single_file_resource_when_extracted_then_success() throws IOException {
        Path path = ClasspathUtils.extractResourcesToTempDirectory("test_file", "extraction_test", this.getClass());
        assertTrue(Files.exists(path), "Temp directory exists");
        assertTrue(Files.exists(path.resolve("test_file")), "Extracted file exists");
        assertEquals("contents of test_file", Files.readAllLines(path.resolve("test_file")).get(0), "Extracted file has correct contents");
    }

    @Test
    void given_directory_resource_when_extracted_then_success() throws IOException {
        Path path = ClasspathUtils.extractResourcesToTempDirectory("test_dir", "extraction_test", this.getClass());
        assertTrue(Files.exists(path), "Temp directory exists");
        assertTrue(Files.exists(path.resolve("test_dir")), "Extracted directory exists");
        assertTrue(Files.exists(path.resolve("test_dir").resolve("test_dir_file")), "Extracted file exists");
        assertEquals("contents of test_dir_file", Files.readAllLines(path.resolve("test_dir").resolve("test_dir_file")).get(0), "Extracted file has correct contents");
    }

    @Test
    void given_nested_zip_when_visited_success() throws URISyntaxException, IOException {
        URI zip1 = Objects.requireNonNull(this.getClass().getClassLoader().getResource("test_zip.zip")).toURI();
        ClasspathUtils.visitUriAsPath(zip1, (ClasspathUtils.PathConsumer<IOException>) path -> assertTrue(Files.exists(path), "Outer zip exists"));

        URI zip1File = new URI("jar", zip1.toString() + "!/test_zip_file", null);
        ClasspathUtils.visitUriAsPath(zip1File, (ClasspathUtils.PathConsumer<IOException>) path -> assertTrue(Files.exists(path), "Outer zip file exists"));

        URI zip2 = new URI("jar", zip1.toString() + "!/test_nested_zip.zip", null);
        ClasspathUtils.visitUriAsPath(zip2, (ClasspathUtils.PathConsumer<IOException>) path -> assertTrue(Files.exists(path), "Inner zip exists"));

        URI zip2File = new URI("jar", zip2.toString() + "!/test_nested_zip_file", null);
        ClasspathUtils.visitUriAsPath(zip2File, (ClasspathUtils.PathConsumer<IOException>) path -> assertTrue(Files.exists(path), "Inner zip file exists"));
    }
}
