package com.twitter.contrib.yatc.dagger.modules;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.Context;

import com.twitter.contrib.yatc.MyApplication;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                MoshiModule.class,
                ServiceModule.class,
                OkHttpModule.class
        }
)
public final class AppModule {

    private final MyApplication app;

    public AppModule(MyApplication app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public Application provideApplication() {
        return app;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return app;
    }

    @Provides
    @Singleton
    public AccountManager provideAccountManager() {
        return AccountManager.get(app);
    }

    @Provides
    @Named("default")
    public Account provideDefaultAccount(AccountManager accountManager) {
        final Account[] accounts = accountManager.getAccountsByType("com.twitter.contrib.yatc.account");

        return accounts != null && accounts.length > 0 ? accounts[0] : null;
    }

}
