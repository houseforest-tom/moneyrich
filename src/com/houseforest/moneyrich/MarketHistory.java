package com.houseforest.moneyrich;

import java.util.HashMap;

/**
 * Created by Tom on 27.11.2015.
 */
public class MarketHistory {

    // Share histories indexed by stock symbol.
    private HashMap<String, ShareHistory> shareHistories;

    public MarketHistory() {
        this.shareHistories = new HashMap<>();
    }

    public ShareHistory getShareHistory(String symbol) {
        return shareHistories.get(symbol);
    }

    public void addShareHistory(String symbol) {
        shareHistories.put(symbol, new ShareHistory(symbol));
    }
}
