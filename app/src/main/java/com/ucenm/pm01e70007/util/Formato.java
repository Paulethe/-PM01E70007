package com.ucenm.pm01e70007.util;

public final class Formato {
    private Formato() {}

    public static String telefono(String t) {
        if (t != null && t.length() == 8) {
            return t.substring(0, 4) + " " + t.substring(4);
        }
        return t;
    }
}
