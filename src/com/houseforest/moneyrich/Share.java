package com.houseforest.moneyrich;

/**
 * Created by Tom on 27.11.2015.
 */
public class Share {

    private String symbol;
    private double value;

    public Share(String symbol, double value) {
        this.symbol = symbol;
        this.value = value;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{ symbol: " + symbol + ", value: " + Main.formatPrice(value) + " }";
    }
}
