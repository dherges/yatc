package com.twitter.contrib.yatc.dagger.modules;

import android.app.Application;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.MoshiConverterFactory;

@Module
public class OkHttpModule {

    @Provides
    @Singleton
    public Cache provideCache(Application application) {
        final File file = new File(application.getCacheDir(), "http");

        return new Cache(file, 16 * 1024 * 1024);
    }

    @Provides
    @Singleton
    @Named("default")
    public OkHttpClient provideDefaultOkHttpClient(Cache cache) {
        final OkHttpClient httpClient = new OkHttpClient();
        httpClient.setCache(cache);

        return new OkHttpClient();
    }

    @Provides
    @Named("custom")
    public OkHttpClient provideCustomOkHttpClient() {
        return new OkHttpClient();
    }

}
