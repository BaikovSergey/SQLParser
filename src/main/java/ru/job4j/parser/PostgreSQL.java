package ru.job4j.parser;

import java.sql.*;

public class PostgreSQL implements Store {
    private final ConfigSQLRuParser configSQLRuParser;
    private Connection connection;

    public PostgreSQL() {
        this.configSQLRuParser = new ConfigSQLRuParser();
    }

    @Override
    public boolean connectToDB() {
        try {
            Class.forName(this.configSQLRuParser.get("driver-class-name"));
            this.connection = DriverManager.getConnection(
                    this.configSQLRuParser.get("url"),
                    this.configSQLRuParser.get("username"),
                    this.configSQLRuParser.get("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return this.connection != null;
    }

    @Override
    public void insert(String name, String text, String date, String link) {
        String sql = "INSERT INTO vacancy (name, text, date, link) VALUES (?, ?, ?, ?);";
        try (PreparedStatement insert = this.connection.prepareStatement(sql)) {
            insert.setString(1, name);
            insert.setString(2, text);
            insert.setString(3, date);
            insert.setString(4, link);
            insert.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean selectFirstElement() {
        boolean result = false;
        String sql = "SELECT * FROM vacancy LIMIT 1;";
        try (PreparedStatement select = this.connection.prepareStatement(sql)) {
            ResultSet resultSet = select.executeQuery();
            if (resultSet.next()) {
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void createTable() {
        String sql = "CREATE TABLE vacancy ("
                + "id SERIAL PRIMARY KEY,"
                + "name VARCHAR(200),"
                + "text TEXT, "
                + "date VARCHAR(20),"
                + "link TEXT);";
        try (PreparedStatement create = this.connection.prepareStatement(sql)) {
            create.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method check if table 'vacancy' exists
     * @return code>true</code> if table 'vacancy' exists;
     * <code>false</code> if table 'vacancy' doesn't exists
     */
    @Override
    public boolean tableExists() {
        boolean result = false;
        try {
            DatabaseMetaData dbm = this.connection.getMetaData();
            ResultSet table = dbm.getTables(null, null, "vacancy", null);
            if (table.next()) {
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
