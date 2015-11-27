package com.houseforest.moneyrich;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tom on 26.11.2015.
 */
public class FormData extends HashMap<String, Object> {

    private String charset;

    public FormData(String charset) {
        this.charset = charset;
    }

    public void add(String name, Object value) {
        put(name, value);
    }

    public String encode() {
        String encoded = "";
        try {
            for (Map.Entry<String, Object> pair : this.entrySet()) {
                if (!encoded.isEmpty()) encoded += "&";
                encoded += pair.getKey() + "=" + URLEncoder.encode(pair.getValue().toString(), charset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encoded;
    }
}
