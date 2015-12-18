package com.twitter.contrib.yatc.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

public class MySyncAdapter extends AbstractThreadedSyncAdapter {

    public MySyncAdapter(Context context) {
        super(context, true, false);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

    }
}
