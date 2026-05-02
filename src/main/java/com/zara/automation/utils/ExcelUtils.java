package com.zara.automation.utils;

import com.zara.automation.config.ConfigReader;
import com.zara.automation.core.exception.TestDataException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Reads cell values from .xlsx test-data files.
 * If the target file does not exist it is created automatically with default values
 * so the test suite can run without manual setup.
 */
public final class ExcelUtils {

    private static final Logger log = LogManager.getLogger(ExcelUtils.class);

    /** Default test-data file path; overridable via {@code TEST_DATA_FILE} in {@code .env}. */
    public static final String DEFAULT_FILE = ConfigReader.getInstance()
            .get("TEST_DATA_FILE", "src/test/resources/test-data/search-terms.xlsx");

    private ExcelUtils() {}

    /**
     * Returns the string value of a cell.
     *
     * @param filePath    path to the .xlsx file
     * @param rowIndex    0-based row index
     * @param columnIndex 0-based column index
     */
    public static String getCellValue(String filePath, int rowIndex, int columnIndex) {
        ensureFileExists(filePath);
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                throw new TestDataException(
                        "Row " + rowIndex + " does not exist in " + filePath);
            }
            Cell cell = row.getCell(columnIndex);
            if (cell == null) {
                throw new TestDataException(
                        "Cell [row=" + rowIndex + ", col=" + columnIndex + "] is empty in " + filePath);
            }
            String value = asString(cell);
            log.info("Excel read → file='{}' row={} col={} value='{}'",
                    filePath, rowIndex, columnIndex, value);
            return value;

        } catch (IOException e) {
            throw new TestDataException("Cannot read Excel file: " + filePath, e);
        }
    }

    /**
     * Convenience method that reads from the default test-data file.
     * Uses 0-based indexing (row 0 = first row, col 0 = first column).
     */
    public static String read(int rowIndex, int columnIndex) {
        return getCellValue(DEFAULT_FILE, rowIndex, columnIndex);
    }

    // ── Internals ─────────────────────────────────────────────────────────────

    /**
     * Converts an Apache POI {@link Cell} to a plain string regardless of its type.
     * Numeric values are truncated to long to strip the trailing {@code .0}.
     *
     * @param cell the cell to read
     * @return string representation of the cell value, or {@code ""} for blank/error cells
     */
    private static String asString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:  return cell.getStringCellValue().trim();
            case NUMERIC: return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA: return cell.getCellFormula();
            default:      return "";
        }
    }

    /**
     * Checks whether the file at {@code filePath} exists; if not, delegates to
     * {@link #createDefaultFile(String)} so tests can run without manual data setup.
     *
     * @param filePath path to the .xlsx file to verify
     */
    private static void ensureFileExists(String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            log.warn("Test-data file '{}' not found — creating with defaults.", filePath);
            createDefaultFile(filePath);
        }
    }

    /**
     * Creates a minimal search-terms.xlsx so tests can run out-of-the-box.
     *
     * Layout (no header — data starts at row 1 / index 0 per the requirement):
     *   A1 (row=0, col=0) → "şort"    (sütun 1, satır 1)
     *   B1 (row=0, col=1) → "gömlek"  (sütun 2, satır 1)
     */
    private static void createDefaultFile(String filePath) {
        try {
            Path dir = Paths.get(filePath).getParent();
            if (dir != null) {
                Files.createDirectories(dir);
            }
            try (Workbook wb = new XSSFWorkbook()) {
                Sheet sheet = wb.createSheet("SearchTerms");

                Row data = sheet.createRow(0);
                data.createCell(0).setCellValue("şort");
                data.createCell(1).setCellValue("gömlek");

                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    wb.write(fos);
                }
                log.info("Created default test-data file: {}", filePath);
            }
        } catch (IOException e) {
            throw new TestDataException("Cannot create test-data file: " + filePath, e);
        }
    }
}
