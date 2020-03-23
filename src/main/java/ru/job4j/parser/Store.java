package ru.job4j.parser;

public interface Store {

    void insert(String name, String text, String date, String link);

    boolean selectFirstElement();

    void createTable();

    boolean tableExists();

    boolean connectToDB();
}
