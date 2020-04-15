package com.skaggsm;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
