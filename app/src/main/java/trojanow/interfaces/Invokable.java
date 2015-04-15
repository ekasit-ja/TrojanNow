package trojanow.interfaces;

import android.content.Intent;

/**
 * Created by Eirik Skogstad.
 */
public interface Invokable {

    /**
     * Called when the class receives an intent through a BroadcastReceiver
     */
    void onReceiveIntent(Intent intent);
}
