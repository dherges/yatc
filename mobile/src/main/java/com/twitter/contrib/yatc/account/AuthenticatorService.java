package com.twitter.contrib.yatc.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Glue-code service to bind an Authenticator to Android's framework
 */
public class AuthenticatorService extends Service {

    private static MyAccountAuthenticator sAccountAuthenticator = null;

    @Override
    public IBinder onBind(Intent intent) {
        if (sAccountAuthenticator == null) {
            sAccountAuthenticator = new MyAccountAuthenticator(this);
        }

        return sAccountAuthenticator.getIBinder();
    }

}
