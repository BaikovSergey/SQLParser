package ru.job4j.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class SQLRuParserConfig {

    /**
     * Log log4J
     */
    static final Logger LOG = LogManager.getLogger(MainProgram.class.getName());

    /**
     * Contains user agents.
     */
    private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            + "AppleWebKit/537.36 (KHTML, like Gecko) "
            + "Chrome/75.0.3770.100 "
            + "Safari/537.36 OPR/62.0.3331.72";
    /**
     * Contains referrer
     */
    private final String referrer = "http://www.google.com";

    /**
     * Contains HTML page www.sql.ru/forum/job-offers
     */
    private Document document = null;

    public Document getDocument(int page) {
        fillTheDocument(page);
        return this.document;
    }

    /**
     * Method gets date from www.sql.ru/forum/job-offers/ page and puts it into this.document.
     * @param page number of page.
     */
     private void fillTheDocument(int page) {
        String url = "https://www.sql.ru/forum/job-offers/" + page;
        try {
            this.document = Jsoup.connect(url)
                    .userAgent(this.userAgent)
                    .referrer(this.referrer)
                    .get();
            LOG.info("HTML page received");
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("HTML page not found");
        }
    }
}
