package com.houseforest.moneyrich;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

public class Main {

    private static final String charset = java.nio.charset.StandardCharsets.UTF_8.name();

    private static final String loginURL = "https://www.onvista.de/login.html";
    private static final String depotURL = "http://my.onvista.de/musterdepot/";

    public class Cookie {

        public String name;
        public String value;

        public Cookie(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    public Main() {

        Vector<Cookie> cookies;
        String cookieString;

        // Post login request.
        HttpURLConnection connection = connect(loginURL);
        System.out.println("Sending Login Request to " + loginURL);
        System.out.println(sendLoginRequest(connection));

        // Retrieve cookies.
        System.out.println("Server assigned the following Cookies:");
        for (Cookie cookie : cookies = extractCookies(connection)) {
            System.out.println(cookie.name + " = " + cookie.value);
        }
        cookieString = concatenateCookies(cookies);
        System.out.println("Using Cookie Value: " + cookieString);

        // Get depot site.
        System.out.println("\nConnecting to depot site " + depotURL);
        connection = connect(depotURL);
        connection.setRequestProperty("Cookie", cookieString);
        System.out.println(sendDepotViewRequest(connection));
    }

    private Vector<Cookie> extractCookies(HttpURLConnection connection) {
        Vector<Cookie> cookies = new Vector<>();
        String key = null, value = null, cookieString = null;

        for (int headerFieldId = 1; ((key = connection.getHeaderFieldKey(headerFieldId)) != null); ++headerFieldId) {
            if (key.equals("Set-Cookie")) {
                if ((value = connection.getHeaderField(headerFieldId)) != null) {
                    cookieString = value.substring(0, value.indexOf(";"));
                    cookies.add(new Cookie(
                            cookieString.substring(0, cookieString.indexOf("=")),
                            cookieString.substring(1 + cookieString.indexOf("="), cookieString.length()))
                    );
                }
            }
        }

        return cookies;
    }

    private String concatenateCookies(Vector<Cookie> cookies) {
        String str = "";
        for (int cookieId = cookies.size() - 1; cookieId >= 0; --cookieId) {
            if (!str.contains(cookies.get(cookieId).name)) {
                if (!str.isEmpty()) str += "; ";
                str += cookies.get(cookieId).name + "=" + cookies.get(cookieId).value;
            }
        }
        return str;
    }

    private String readToEnd(InputStream stream) {
        String result = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                result += line + "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String readResponse(HttpURLConnection connection) {
        String result = "";
        String encoding = "";

        try {
            InputStream stream = connection.getInputStream();

            // Read encoding type.
            if (connection.getHeaderFields().containsKey("Content-Encoding")) {
                encoding = connection.getHeaderField("Content-Encoding").toUpperCase();
            }

            switch (encoding) {
                case "GZIP":
                    InputStream unzipped = new BufferedInputStream(new GZIPInputStream(stream));
                    return readToEnd(unzipped);
                default:
                    return readToEnd(stream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private String httpGet(HttpURLConnection connection) {
        try {
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            return connection.getResponseCode()
                    + " - " + connection.getResponseMessage()
                    + ":\n" + readResponse(connection);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String httpPost(HttpURLConnection connection, FormData data) {
        try {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            OutputStream stream = connection.getOutputStream();
            stream.write(data.encode().getBytes(charset));
            return connection.getResponseCode()
                    + " - " + connection.getResponseMessage()
                    + ":\n" + readResponse(connection);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private HttpURLConnection connect(String url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setReadTimeout((int) 1E4);
            connection.setConnectTimeout((int) 1E4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    private void crash(String msg) {
        System.err.println(msg);
        System.exit(-1);
    }

    private String sendLoginRequest(HttpURLConnection connection) {

        // Set request header fields.
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        connection.setRequestProperty("Accept_Language", "de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4");
        connection.setRequestProperty("Cache-Control", "max-age=0");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Content-Length", "125");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("DNT", "1");
        connection.setRequestProperty("Host", "www.onvista.de");
        connection.setRequestProperty("Origin", "https://kunde.onvista.de");
        connection.setRequestProperty("Referer", "https://kunde.onvista-bank.de/login.html?targetPageToken=%2Fmusterdepot%2F");
        connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");

        // Populate form data.
        FormData data = new FormData(charset);
        data.put("invest", "ja");
        data.put("env", "onvista");
        data.put("targetPageToken", "/musterdepot/");
        data.put("targetPageQuery", "");
        data.put("login", "thauswald");
        data.put("password", "bucherregal123");
        data.put("__send", "Login");

        return httpPost(connection, data);
    }

    private String sendDepotViewRequest(HttpURLConnection connection) {

        // Set request header fields.
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
        connection.setRequestProperty("Accept_Language", "de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4");
        connection.setRequestProperty("Cache-Control", "max-age=0");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("DNT", "1");
        connection.setRequestProperty("Host", "my.onvista.de");
        connection.setRequestProperty("Referer", "http://my.onvista.de");
        connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");

        return httpGet(connection);
    }

    public static void main(String[] args) {
        new Main();
    }
}
