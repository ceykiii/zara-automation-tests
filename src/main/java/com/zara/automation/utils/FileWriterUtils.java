package com.zara.automation.utils;

import com.zara.automation.config.ConfigReader;
import com.zara.automation.core.exception.TestDataException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Writes selected product information to a human-readable text file.
 * Output directory is read from {@code TEST_OUTPUT_DIR} in {@code .env}
 * (default: {@code target/test-output}).
 */
public final class FileWriterUtils {

    private static final Logger log = LogManager.getLogger(FileWriterUtils.class);
    private static final String OUTPUT_FILE_NAME = "product-info.txt";
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private FileWriterUtils() {}

    /**
     * Clears the product-info output file so each test run starts fresh.
     * Call this once from a {@code @BeforeSuite} or {@code @BeforeAll} hook.
     *
     * @throws TestDataException if the file cannot be cleared
     */
    public static void clearProductInfo() {
        String outputDir  = ConfigReader.getInstance().get("TEST_OUTPUT_DIR", "target/test-output");
        Path   outputPath = Paths.get(outputDir, OUTPUT_FILE_NAME);

        try {
            Files.createDirectories(Paths.get(outputDir));
            Files.writeString(outputPath, "");
            log.info("Product info file cleared: {}", outputPath.toAbsolutePath());
        } catch (IOException e) {
            throw new TestDataException("Failed to clear product info file: " + outputPath, e);
        }
    }

    /**
     * Appends product details to the configured output file.
     * The output directory is created automatically if it does not exist.
     *
     * @param productName  display name of the selected product
     * @param productPrice price text captured from the UI
     * @return absolute path of the output file
     * @throws TestDataException if the file cannot be written
     */
    public static String writeProductInfo(String productName, String productPrice) {
        String outputDir  = ConfigReader.getInstance().get("TEST_OUTPUT_DIR", "target/test-output");
        Path   outputPath = Paths.get(outputDir, OUTPUT_FILE_NAME);

        try {
            Files.createDirectories(Paths.get(outputDir));

            try (PrintWriter writer = new PrintWriter(
                    new FileWriter(outputPath.toFile(), true))) {   // append mode
                writer.println("==============================");
                writer.println("  SELECTED PRODUCT INFO");
                writer.println("==============================");
                writer.println("Date    : " + LocalDateTime.now().format(FMT));
                writer.println("Product : " + productName);
                writer.println("Price   : " + productPrice);
                writer.println();
            }

            log.info("Product info written to file: {}", outputPath.toAbsolutePath());
            return outputPath.toAbsolutePath().toString();

        } catch (IOException e) {
            throw new TestDataException("Failed to write product info to file: " + outputPath, e);
        }
    }
}
