package com.houseforest.moneyrich;

/**
 * Created by Tom on 27.11.2015.
 */
public class Position {

    private Share share;
    private int count;

    public Position(Share share, int count){
        this.share = share;
        this.count = count;
    }

    public Share getShare(){
        return share;
    }

    public int getCount(){
        return count;
    }

    public double getCombinedValue() {
        return share.getValue() * count;
    }

    @Override
    public String toString() {
        return "{ share: " + share.toString() +
                ", count: " + count
                + ", combined: " + Main.formatPrice(getCombinedValue()) + " }";
    }
}
