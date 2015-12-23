package com.twitter.contrib.yatc.dagger.modules;

import com.squareup.moshi.Moshi;
import com.squareup.okhttp.OkHttpClient;
import com.twitter.contrib.yatc.http.BasicAuthenticationInterceptor;
import com.twitter.contrib.yatc.http.oauth.OAuth2Service;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.MoshiConverterFactory;
import retrofit.Retrofit;

@Module(
        includes = {
                OkHttpModule.class,
                MoshiModule.class
        }
)
public class TwitterModule {

    @Provides
    @Singleton
    public OAuth2Service provideOAuth2Service (OkHttpClient okHttpClient, Moshi moshi) {
        final OkHttpClient okHttpClient1 = okHttpClient.clone();
        okHttpClient1.networkInterceptors()
                .add(new BasicAuthenticationInterceptor(Config.CONSUMER_KEY, Config.CONSUMER_SECRET));

        return new Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl(Config.BASE_URL)
                .client(okHttpClient1)
                .build()
                .create(OAuth2Service.class);
    }

    public interface Config {
        String BASE_URL = "https://api.twitter.com";
        String CONSUMER_KEY = "vp3R3eeSvXcYAJLot3TJOE1SJ";
        String CONSUMER_SECRET = "qqI5GFRqJCnHFiIaK10gyVqDhrvGftZFUNIfO7bWGiSvhIyoM0";
    }
}
