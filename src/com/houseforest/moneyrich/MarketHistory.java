package com.houseforest.moneyrich;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by Tom on 27.11.2015.
 */
public class MarketHistory {

    public void update(Browser browser) {
        String fetchURL = "finance.yahoo.com/...";
        String response = browser.sendGetRequest(browser.createHTTPConnection(fetchURL));
        Document doc = Jsoup.parse(response);
    }
}
