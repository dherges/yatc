package com.twitter.contrib.yatc.dagger;

import android.content.Context;

public final class Injector {

    private Injector() {
        throw new AssertionError("No instances.");
    }

    public static AppComponent obtainApp(Context context) {
        if (context.getApplicationContext() instanceof AppComponentCreator) {
            return ((AppComponentCreator) context.getApplicationContext()).getComponent();
        }

        throw new IllegalStateException("Context must be an instance of AppComponentCreator!");
    }

    /*
    public static ActivityComponent obtainActivity(Context context) {
        if (context instanceof ActivityComponentCreator) {
            return ((ActivityComponentCreator) context).getComponent();
        }

        throw new IllegalStateException("Context must be an instance of ActivityComponentCreator!");
    }
    */

}
