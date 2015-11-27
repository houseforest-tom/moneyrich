package com.houseforest.moneyrich;

/**
 * Created by Tom on 27.11.2015.
 */
public class Position {

    public Share share = new Share();
    public int count = 0;

    public double getCombinedValue() {
        return share.value * count;
    }

    @Override
    public String toString() {
        return "{ share: " + share.toString() +
                ", count: " + count
                + ", combined: " + Main.formatPrice(getCombinedValue()) + " }";
    }
}
