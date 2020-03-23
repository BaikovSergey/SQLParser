package ru.job4j.parser;

import org.jsoup.nodes.Document;

import java.util.ArrayList;

public interface Parse {

    ArrayList<String> parseData(Document data);
}
