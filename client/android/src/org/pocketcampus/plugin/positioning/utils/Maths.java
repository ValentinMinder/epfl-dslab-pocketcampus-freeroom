package org.pocketcampus.plugin.positioning.utils;

/**
 * Author : Jama package * modified by Tarek
 * 
 * */

public class Maths {

    public Maths() {
    }

    public static double hypot(double d, double d1) {
        double d2;
        if(Math.abs(d) > Math.abs(d1)) {
            d2 = d1 / d;
            d2 = Math.abs(d) * Math.sqrt(1.0D + d2 * d2);
        } else
        if(d1 != 0.0D) {
            d2 = d / d1;
            d2 = Math.abs(d1) * Math.sqrt(1.0D + d2 * d2);
        } else {
            d2 = 0.0D;
        }
        return d2;
    }
}
