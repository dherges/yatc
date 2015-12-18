package com.twitter.contrib.yatc.dagger.modules;

import com.squareup.moshi.Moshi;
import dagger.Module;
import dagger.Provides;
import retrofit.MoshiConverterFactory;

@Module()
public class ServiceModule {

    @Provides
    public MoshiConverterFactory provideMoshiConverterFactory(Moshi moshi) {
        return MoshiConverterFactory.create(moshi);
    }

}
