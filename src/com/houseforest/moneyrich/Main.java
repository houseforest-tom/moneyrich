package com.houseforest.moneyrich;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.HashMap;

public class Main {

    // Onvista share links by Yahoo! Finance compliant stock symbol.
    public static final HashMap<String, String> shareLinks = new HashMap<>();

    private Browser browser;
    private Depot depot;

    public Main() {

        String username = "", password = "";

        browser = new Browser(java.nio.charset.StandardCharsets.UTF_8.name());
        depot = new Depot();

        System.out.println("Fetching market data...");

        // Fetch market data.
        MarketHistory history = new MarketHistory();
        for (String symbol : Main.shareLinks.keySet()) {
            history.addShareHistory(symbol);
            history.getShareHistory(symbol).fetch(browser);
            System.out.println(" -> " + history.getShareHistory(symbol).getLatestShare());
        }

        // Prompt user login data.
        try {
            System.out.println("Welcome to the moneyrich Depot Manager!");
            username = readLine("Username: ");
            password = String.valueOf(readPassword("Password: "));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Connect to depot and print current status.
        depot.login(browser, username, password);
        depot.update(browser);
        depot.dump();
    }

    public static String formatPrice(double value) {
        return new DecimalFormat("#0.00€").format(value);
    }

    public static double parsePrice(String value) {
        value = value.trim();
        value = value.split(" ")[0];            // Stop parsing before first space.
        value = value.replaceAll("\\.", "");    // Replace separator dots.
        value = value.replaceAll(",", ".");     // Replace mantissa comma with dot.
        return Double.parseDouble(value);
    }

    private String readLine(String format, Object... args) throws IOException {
        if (System.console() != null) {
            return System.console().readLine(format, args);
        }
        System.out.print(String.format(format, args));
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }

    private char[] readPassword(String format, Object... args)
            throws IOException {
        if (System.console() != null)
            return System.console().readPassword(format, args);
        return this.readLine(format, args).toCharArray();
    }

    public static void main(String[] args) {

        // Construct mapping between Yahoo! Finance API and Onvista depot.
        Main.shareLinks.put("APC.DE", "http://www.onvista.de/aktien/Apple-Aktie-US0378331005");
        Main.shareLinks.put("BMW.DE", "http://www.onvista.de/aktien/BMW-Aktie-DE0005190003");
        Main.shareLinks.put("ABEA.DE", "http://www.onvista.de/aktien/Alphabet-Inc-A-ehemals-Google-Aktie-US02079K3059");
        Main.shareLinks.put("LHA.DE", "http://www.onvista.de/aktien/Lufthansa-Aktie-DE0008232125");
        Main.shareLinks.put("DAI.DE", "http://www.onvista.de/aktien/Daimler-Aktie-DE0007100000");

        new Main();
    }
}