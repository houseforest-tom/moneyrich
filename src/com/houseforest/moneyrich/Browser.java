package com.houseforest.moneyrich;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

/**
 * Created by Tom on 27.11.2015.
 */
public class Browser {

    private String charset;

    public Browser(String charset) {
        this.charset = charset;
    }

    public String getCharset() {
        return charset;
    }

    public Vector<Cookie> extractCookies(HttpURLConnection connection) {
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

    public String concatenateCookies(Vector<Cookie> cookies) {
        String str = "";
        for (int cookieId = cookies.size() - 1; cookieId >= 0; --cookieId) {
            if (!str.contains(cookies.get(cookieId).name)) {
                if (!str.isEmpty()) str += "; ";
                str += cookies.get(cookieId).name + "=" + cookies.get(cookieId).value;
            }
        }
        return str;
    }

    private String readCompleteStream(InputStream stream) {
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

    public String readResponse(HttpURLConnection connection) {
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
                    return readCompleteStream(unzipped);
                default:
                    return readCompleteStream(stream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public String sendGetRequest(HttpURLConnection connection) {
        try {
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            return readResponse(connection);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String sendPostRequest(HttpURLConnection connection, FormData data) {
        try {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            OutputStream stream = connection.getOutputStream();
            stream.write(data.encode().getBytes(charset));
            return readResponse(connection);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public HttpURLConnection createHTTPConnection(String url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setReadTimeout((int) 1E4);
            connection.setConnectTimeout((int) 1E4);
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            connection.setRequestProperty("Accept_Language", "de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4");
            connection.setRequestProperty("Cache-Control", "max-age=0");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("DNT", "1");
            connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}