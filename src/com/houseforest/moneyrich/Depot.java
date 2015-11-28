package com.houseforest.moneyrich;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Tom on 27.11.2015.
 */
public class Depot {

    private static final String loginWebsiteURL = "https://www.onvista.de/login.html";
    private static final String depotWebsiteURL = "http://my.onvista.de/musterdepot/";

    private double cash;
    private LinkedList<Position> positions;
    private String cookie;

    public Depot() {
        positions = new LinkedList<>();
    }

    public LinkedList<Position> getPositions() {
        return positions;
    }

    public double getCash() {
        return cash;
    }

    public double getCombinedPositionsValue() {
        double combined = 0.0;
        for (Position pos : positions) combined += pos.getCombinedValue();
        return combined;
    }

    public double getValue() {
        return cash + getCombinedPositionsValue();
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

        // Traverse DOM.
        Document doc = Jsoup.parse(depotSiteSource);
        Element dashboard = doc.getElementsByClass("DEPOT_KENNZAHLEN_02").get(0);
        Element table = doc.getElementById("myoPortfolioPageTabNavigationBoxAction");
        Elements rows = table.getElementsByClass("HAUPTZEILE");

        // Parse depot dashboard.
        parseDashboard(dashboard);

        // Parse positions.
        for (Element row : rows) {
            positions.add(parseTableRow(row));
        }
    }

    private void parseDashboard(Element dashboard) {
        cash = Main.parsePrice(dashboard.getElementsByClass("ZAHL").get(1).text());
    }

    private Position parseTableRow(Element row) {

        // Parse count.
        int count = Integer.parseInt(row.getElementsByClass("ZAHL").get(0).text());

        // Parse link.
        String href = row.getElementsByClass("TEXT").get(0)
                .getElementsByTag("a").get(0)
                .attr("href").trim();


        // Parse share value.
        double value = Main.parsePrice(
                row.getElementsByClass("TEXT").get(2)
                        .getElementsByTag("span").get(0)
                        .text()
        );

        for(Map.Entry<String, String> symlink : Main.shareLinks.entrySet()){
            if(symlink.getValue().equals(href)){
                return new Position(new Share(symlink.getKey(), value), count);
            }
        }

        System.out.println("Error: No matching symbol found for link: " + href);
        return null;
    }

    public void dump() {
        System.out.println("\n********* Depot Information *********");
        for (Position pos : positions) System.out.println("[Position] " + pos.toString());
        System.out.println("-+------------------------+-");
        System.out.println("Positions Total:\t" + Main.formatPrice(getCombinedPositionsValue()));
        System.out.println("-+------------------------+-");
        System.out.println("Available Cash:\t\t" + Main.formatPrice(cash));
        System.out.println("-+------------------------+-");
        System.out.println("Depot Total:\t\t" + Main.formatPrice(getValue()));
        System.out.println("*************************************");
    }
}
