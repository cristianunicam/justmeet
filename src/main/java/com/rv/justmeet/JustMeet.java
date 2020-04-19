package com.rv.justmeet;

import com.rv.justmeet.main.core.SoftwareManager;

public class JustMeet {
    public static void main(String[] args) {
        start();
        System.exit(0);
    }

    private static void start() {
        SoftwareManager manager = new SoftwareManager();
        manager.inizializzaSoftware();
    }
}