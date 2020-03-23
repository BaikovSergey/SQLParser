package ru.job4j.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SQLRuParser implements Parse {

    private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            + "AppleWebKit/537.36 (KHTML, like Gecko) "
            + "Chrome/75.0.3770.100 "
            + "Safari/537.36 OPR/62.0.3331.72";
    /**
     * Contains referrer
     */
    private final String referrer = "http://www.google.com";

    /**
     * Log log4J
     */
    static final Logger LOG = LogManager.getLogger(MainProgram.class.getName());

    /**
     * Method parses given HTML page for Java vacancies and excludes JavaScript then adds it to list.
     * @param data HTML page.
     * @return List of vacancy
     */
    @Override
    public ArrayList<String> parseData(Document data) {
        ArrayList<String> list = new ArrayList<>();
        Elements vacancies = data.select("table.forumTable").select("tr").next();
        for (Element el : vacancies) {
            String vacancyName = el.select("a[href]").text();
            String vacancyLink = el.select("a[href]").attr("href");
            String vacancyDate = el.select("td.altCol").last().text();
            String vacancyText = getVacancyText(vacancyLink);
            int vacancyYear = getYear(vacancyDate);
            if (vacancyName.contains("Java")
                    || vacancyName.contains("java")
                    || vacancyName.contains("JAVA")
                    && (!vacancyName.contains("Script"))
                    && (!vacancyName.contains("script"))
                    && (!vacancyName.contains("SCRIPT"))
                    && vacancyYear >= getYear(vacancyDate)) {
                list.add(0, vacancyName);
                list.add(1, vacancyText);
                list.add(2, vacancyDate);
                list.add(3, vacancyLink);
            }
        }
        LOG.info("Vacancy has been added to list");
        return list;
    }

    /**
     * Method gets vacancy name
     * @param url https://www.sql.ru/forum/job-offers/
     * @return vacancy text
     */
    private String getVacancyText(String url) {
        String result = "";
        Document document = null;
        try {
            document = Jsoup.connect(url)
                    .userAgent(this.userAgent)
                    .referrer(this.referrer)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (document != null) {
            Elements text = document.select("table.msgTable").select("tr").select("td.msgBody");
            if (text != null) {
                result = text.first().nextElementSibling().text();
            }
        }
        return result;
    }



    /**
     * Method trim incoming data time to two last digits of the year. If date time contains words 'сегодня' or 'вчера',
     * method returns current year.
     * Example: 27 июл 19, 12:45 -> 19
     * @param date date time
     * @return trimmed date time
     */
    protected int getYear(String date) {
        int result = 0;
        String temp = date.substring(0, date.length() - 7);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yy");
        LocalDateTime now = LocalDateTime.now();
        if (temp.equals("сегодня") || temp.equals("вчера")) {
            result = Integer.parseInt(dtf.format(now));
        } else {
            result = Integer.parseInt(temp.substring(temp.length() - 2));
        }
        return result;
    }

}
