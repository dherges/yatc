package com.twitter.contrib.yatc;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.twitter.contrib.yatc.dagger.AppComponent;
import com.twitter.contrib.yatc.dagger.DaggerAppComponent;
import com.twitter.contrib.yatc.dagger.modules.AppModule;

import nl.qbusict.cupboard.CupboardBuilder;
import nl.qbusict.cupboard.CupboardFactory;

public class MyApplication extends Application {

    // TODO register cupboard entities
    static {
        CupboardFactory.setCupboard(new CupboardBuilder()
//                .registerFieldConverter(Message.Body.class, new GsonFieldConverter<>(new Gson(), Message.Body.class))
                .build());

//        cupboard().register(Message.class);
    }

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        LeakCanary.install(this);

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }


}
