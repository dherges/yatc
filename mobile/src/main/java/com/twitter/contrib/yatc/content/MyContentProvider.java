package com.twitter.contrib.yatc.content;

import nl.littlerobots.cupboard.tools.provider.CupboardContentProvider;

public class MyContentProvider extends CupboardContentProvider {

    public static final String AUTHORITY = "com.twitter.contrib.yatc.content";
    private static final String DB_NAME = "content.db";
    private static final int DB_VERSION = 1;

    public MyContentProvider() {
        super(AUTHORITY, DB_NAME, DB_VERSION);
    }
}
