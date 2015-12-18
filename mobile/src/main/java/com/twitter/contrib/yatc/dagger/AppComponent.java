package com.twitter.contrib.yatc.dagger;

import android.content.Context;

import com.twitter.contrib.yatc.account.MyAccountAuthenticator;
import com.twitter.contrib.yatc.dagger.modules.AppModule;
import com.twitter.contrib.yatc.dagger.modules.MoshiModule;
import com.twitter.contrib.yatc.dagger.modules.ServiceModule;
import com.twitter.contrib.yatc.sync.MySyncAdapter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Component is a "Scope", AppComponent is scoped to the lifecycle of the application
 *
 * @link http://fernandocejas.com/2015/04/11/tasting-dagger-2-on-android/
 */
@Component(
        modules = {
                AppModule.class,
                ServiceModule.class,
                MoshiModule.class
        }
)
@Singleton
public interface AppComponent {

    void inject(Context context);

    void inject(MySyncAdapter mySyncAdapter);

    void inject(MyAccountAuthenticator myAccountAuthenticator);


}
