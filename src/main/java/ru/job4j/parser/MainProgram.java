package ru.job4j.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MainProgram implements Job {

    private PostgreSQL postgreSQL = new PostgreSQL();
    private SQLRuParser sqlRuParser = new SQLRuParser();

    /**
     * Log log4J
     */
    static final Logger LOG = LogManager.getLogger(MainProgram.class.getName());

    /**
     * Contains current Year
     */
    private String currentYear = getCurrentYear();

    /**
     * Contains vacancies.
     */
    private List<ArrayList<String>> vacancies = new ArrayList<>();

    /**
     * Method parses www.sql.ru/forum/job-offers/ page for Java vacancies and put them to date base.
     */
    public void start() {
        this.vacancies.clear();
        this.postgreSQL.connectToDB();
        if (!this.postgreSQL.tableExists()) {
            this.postgreSQL.createTable();
        }
        if (!this.postgreSQL.selectFirstElement()) {
                int page = 1;
                while (!getCurrentYear().equals(this.vacancies.get(this.vacancies.size() - 1).get(2))) {
                    SQLRuParserConfig info = new SQLRuParserConfig();
                    this.vacancies.add(sqlRuParser.parseData(info.getDocument(page)));
                    LOG.info("Page " + page + "is parsed");
                    page++;
                }
            LOG.info("First app run");
        } else {
            SQLRuParserConfig info = new SQLRuParserConfig();
            this.vacancies.add(sqlRuParser.parseData(info.getDocument(1)));
                }
           LOG.info("Second app run");
            addToDB(this.vacancies);
    }

    /**
     * Method adds vacancies to DB.
     * @param list this.vacancies.
     */
    private void addToDB(List<ArrayList<String>> list) {
        for (ArrayList<String> vacancy: list
             ) {
            String name = vacancy.get(0);
            String text = vacancy.get(1);
            String date = vacancy.get(2);
            String link = vacancy.get(3);
            if (!date.equals(getCurrentYear())) {
                this.postgreSQL.insert(name, text, date, link);
                LOG.info("Vacancy added to DB");
            }
        }
    }

    /**
     * Method gets current year in format YY
     * @return current year
     */
    private String getCurrentYear() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yy");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        start();
    }

    public static void main(String[] args) {
        MainProgram test = new MainProgram();
        test.start();
    }
}
