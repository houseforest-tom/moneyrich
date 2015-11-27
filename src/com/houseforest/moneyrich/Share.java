package com.houseforest.moneyrich;

/**
 * Created by Tom on 27.11.2015.
 */
public class Share {

    public String name = "";
    public double value = 0.0;

    @Override
    public String toString() {
        return "{ name: " + name + ", value: " + Main.formatPrice(value) + " }";
    }
}
