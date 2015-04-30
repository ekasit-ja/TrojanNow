package usc.cs578.trojannow.manager.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by echo on 4/22/15.
 */
public class NetworkBroadcastListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        intent.setClass(context, NetworkManager.class);
        context.startService(intent);
    }
}
