package com.twitter.contrib.yatc.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Glue-code service to bind a SyncAdapter to Android's framework
 */
public class SyncAdapterService extends Service {

    private static MySyncAdapter sSyncAdapter = null;

    @Override
    public IBinder onBind(Intent intent) {
        if (sSyncAdapter == null) {
            sSyncAdapter = new MySyncAdapter(this);
        }

        return sSyncAdapter.getSyncAdapterBinder();
    }

}
