package com.houseforest.moneyrich;

import java.net.HttpURLConnection;

public class Main {

    private Browser browser;
    private Depot depot;

    public Main() {

        final String username = "thauswald";
        final String password = "bucherregal123";

        browser = new Browser(java.nio.charset.StandardCharsets.UTF_8.name());
        depot = new Depot();
        depot.login(browser, username, password);
        depot.update(browser);
    }

    public static void main(String[] args) {
        new Main();
    }
}
