package com.ryderbelserion.cmd.core;

public class TriumphProvider {

    private static TriumphManager instance = null;

    public static void register(final TriumphManager instance) {
        TriumphProvider.instance = instance;
    }

    public static TriumphManager getInstance() {
        return instance;
    }
}