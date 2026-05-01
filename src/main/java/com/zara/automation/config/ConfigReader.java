package com.zara.automation.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Thread-safe Singleton that reads configuration from .env and system environment.
 * System environment variables take precedence (enables CI/CD overrides).
 */
public final class ConfigReader {

    private static final Logger log = LogManager.getLogger(ConfigReader.class);

    private static volatile ConfigReader instance;
    private final Dotenv dotenv;

    private ConfigReader() {
        dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .load();
        log.info("Environment configuration loaded");
    }

    /**
     * Returns the double-checked-locking singleton, creating it on first call.
     *
     * @return the shared {@link ConfigReader} instance
     */
    public static ConfigReader getInstance() {
        if (instance == null) {
            synchronized (ConfigReader.class) {
                if (instance == null) {
                    instance = new ConfigReader();
                }
            }
        }
        return instance;
    }

    /**
     * Returns the value for {@code key}, preferring the system environment variable
     * over the {@code .env} file. Logs a warning and returns {@code ""} if not found.
     *
     * @param key property key
     * @return resolved value, never {@code null}
     */
    public String get(String key) {
        String sysEnv = System.getenv(key);
        if (sysEnv != null && !sysEnv.isBlank()) {
            return sysEnv;
        }
        String dotenvVal = dotenv.get(key);
        if (dotenvVal == null) {
            log.warn("Config key '{}' not found — returning empty string", key);
            return "";
        }
        return dotenvVal;
    }

    /**
     * Same as {@link #get(String)} but returns {@code defaultValue} when the key is absent
     * instead of an empty string.
     *
     * @param key          property key
     * @param defaultValue fallback value
     * @return resolved value or {@code defaultValue}
     */
    public String get(String key, String defaultValue) {
        String value = dotenv.get(key, defaultValue);
        String sysEnv = System.getenv(key);
        return (sysEnv != null && !sysEnv.isBlank()) ? sysEnv : value;
    }

    /**
     * Reads {@code key} and parses it as an {@code int}. Returns {@code defaultValue}
     * and logs a warning if the value is missing or cannot be parsed.
     *
     * @param key          property key
     * @param defaultValue fallback value
     * @return parsed integer or {@code defaultValue}
     */
    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            log.warn("Cannot parse '{}' as int, using default {}", key, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Reads {@code key} and parses it as a {@code boolean} via {@link Boolean#parseBoolean}.
     *
     * @param key          property key
     * @param defaultValue fallback value
     * @return {@code true} only if the resolved string equals {@code "true"} (case-insensitive)
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(get(key, String.valueOf(defaultValue)));
    }
}
