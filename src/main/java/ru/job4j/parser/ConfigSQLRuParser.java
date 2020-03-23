package ru.job4j.parser;

import java.io.InputStream;
import java.util.Properties;

public class ConfigSQLRuParser {
    private final Properties values = new Properties();

    public ConfigSQLRuParser() {
        init();
    }

    public void init() {
        try (InputStream in = ConfigSQLRuParser.class.getClassLoader().getResourceAsStream("app.properties")) {
            values.load(in);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public String get(String key) {
        return this.values.getProperty(key);
    }
}
