package com.houseforest.moneyrich;

import java.net.HttpURLConnection;
import java.util.LinkedList;

/**
 * Created by Tom on 27.11.2015.
 */
public class Depot {

    private static final String loginWebsiteURL = "https://www.onvista.de/login.html";
    private static final String depotWebsiteURL = "http://my.onvista.de/musterdepot/";

    public class Share {
        public String name;
        public double price;
    }

    public class Position {
        public Share share;
        public int count;

        public double getCombinedValue() {
            return share.price * count;
        }
    }

    private LinkedList<Position> positions;
    private String cookie;

    public Depot() {
        positions = new LinkedList<>();
    }

    public LinkedList<Position> getPositions() {
        return positions;
    }

    public void login(Browser browser, String username, String password) {

        // Populate form data.
        FormData data = new FormData(browser.getCharset());
        data.put("invest", "ja");
        data.put("env", "onvista");
        data.put("targetPageToken", "");
        data.put("targetPageQuery", "");
        data.put("login", username);
        data.put("password", password);
        data.put("__send", "Login");

        // Post login request.
        HttpURLConnection connection = browser.createHTTPConnection(loginWebsiteURL);
        System.out.println("Sending Login Request to " + loginWebsiteURL);
        sendLoginRequest(browser, connection, data);

        // Store cookie value.
        this.cookie = browser.concatenateCookies(browser.extractCookies(connection));
        System.out.println("Assigned Cookie(s): " + this.cookie);
    }

    private String sendLoginRequest(Browser browser, HttpURLConnection connection, FormData data) {

        // Set request header fields.
        connection.setRequestProperty("Content-Length", "125");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Host", "www.onvista.de");
        connection.setRequestProperty("Origin", "https://kunde.onvista.de");
        connection.setRequestProperty("Referer", "https://kunde.onvista-bank.de/login.html?targetPageToken=%2Fmusterdepot%2F");

        return browser.sendPostRequest(connection, data);
    }

    public void update(Browser browser) {

        // Fetch depot site source.
        System.out.println("Fetching depot data from " + depotWebsiteURL);
        HttpURLConnection connection = browser.createHTTPConnection(depotWebsiteURL);
        connection.setRequestProperty("Cookie", this.cookie);
        String depotSiteSource = browser.sendGetRequest(connection);

        // TODO: Extract depot position data.
        System.out.println(depotSiteSource);
    }
}
