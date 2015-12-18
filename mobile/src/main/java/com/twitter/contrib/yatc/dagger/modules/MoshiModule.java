package com.twitter.contrib.yatc.dagger.modules;

import com.squareup.moshi.Moshi;

import dagger.Module;
import dagger.Provides;

@Module()
public class MoshiModule {

    @Provides
    public Moshi provideMoshi() {
        return new Moshi.Builder().build();
    }
}
