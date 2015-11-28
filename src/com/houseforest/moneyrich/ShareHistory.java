package com.houseforest.moneyrich;

import sun.reflect.generics.tree.Tree;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by Tom on 28.11.2015.
 */
public class ShareHistory {
    private String shareSymbol;
    private TreeMap<LocalDate, Double> shareValues;

    public ShareHistory(String symbol) {
        this.shareSymbol = symbol;
        this.shareValues = new TreeMap<>();
    }

    public void fetch(Browser browser, LocalDate since, LocalDate until) {

        // Construct query URL for Yahoo! Finance API.
        String fetchURL = String.format(
                "http://ichart.yahoo.com/table.csv?s=%s&a=%d&b=%d&c=%d&d=%d&e=%d&f=%d&g=d",
                shareSymbol,
                since.getMonthValue() - 1,
                since.getDayOfMonth(),
                since.getYear(),
                until.getMonthValue() - 1,
                until.getDayOfMonth(),
                until.getYear()
        );

        String response = browser.sendGetRequest(browser.createHTTPConnection(fetchURL));
        String[] rows = response.split("\n");
        String[] cols = null;
        String row = null;

        for (int i = 1; i < rows.length; ++i) {
            row = rows[i];
            cols = row.split(",");
            shareValues.put(
                    LocalDate.parse(cols[0]),
                    Double.parseDouble(cols[4])
            );
        }
    }

    public void fetch(Browser browser) {
        fetch(browser, LocalDate.parse("2010-01-01"), LocalDate.now());
    }

    public Share getShare(LocalDate when) {
        return new Share(shareSymbol, shareValues.get(when));
    }

    public Share getLatestShare() {
        return new Share(shareSymbol, shareValues.lastEntry().getValue());
    }
}
