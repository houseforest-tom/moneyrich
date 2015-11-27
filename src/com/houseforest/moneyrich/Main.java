package com.houseforest.moneyrich;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class Main {

    private Browser browser;
    private Depot depot;

    public Main() {

        String username = "", password = "";

        browser = new Browser(java.nio.charset.StandardCharsets.UTF_8.name());
        depot = new Depot();

        try {
            System.out.println("Welcome to the moneyrich Depot Manager!");
            Console console = System.console();
            username = readLine("Username: ");
            password = String.valueOf(readPassword("Password: "));
            depot.login(browser, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        depot.update(browser);
    }

    public static void main(String[] args) {
        new Main();
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