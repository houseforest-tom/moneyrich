package com.houseforest.moneyrich;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

public class Main {

    private Browser browser;
    private Depot depot;

    public Main() {

        String username = "", password = "";

        browser = new Browser(java.nio.charset.StandardCharsets.UTF_8.name());
        depot = new Depot();

        // Prompt user login data.
        try {
            System.out.println("Welcome to the moneyrich Depot Manager!");
            username = readLine("Username: ");
            password = String.valueOf(readPassword("Password: "));
        }
        catch(IOException e){
            e.printStackTrace();
        }

        // Connect to depot and print current status.
        depot.login(browser, username, password);
        depot.update(browser);
        depot.dump();
    }

    public static void main(String[] args) {
        new Main();
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
}