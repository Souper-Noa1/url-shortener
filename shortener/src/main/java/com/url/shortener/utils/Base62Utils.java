package com.url.shortener.utils;

import static java.lang.Math.pow;

public class Base62Utils {
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final int BASE = ALPHABET.length();


    public static String encode(long id) {

        if (id == 0) return String.valueOf(ALPHABET.charAt(0));

        StringBuilder sb = new StringBuilder();

        while (id > 0) {
            sb.append(ALPHABET.charAt((int) (id % BASE)));
            id = id / BASE;
        }

        return sb.reverse().toString();
    }

    public static long decode(String shortKey) {
        long result = 0;

        for (int i = 0; i < shortKey.length(); i++) {

            int x = ALPHABET.indexOf(shortKey.charAt(i));
            result = result * BASE + x;
        }
        return result;
    }
}

