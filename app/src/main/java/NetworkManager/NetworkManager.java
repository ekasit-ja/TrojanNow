package NetworkManager;

import Interfaces.Invokable;
import android.app.IntentService;
import android.content.Intent;

/**
 * PURPOSE:
 * This class is responsible for communicating with the server, and make sure event messages are
 * passed between the client and the server.
 *
 * OPERATION:
 * This class will listen for events from components and send them to the server. It will also poll event messages
 * from the server and distribute them to the components. For this it will use the BroadcastReceiver and
 * LocalBroadcastManager.
 *
 * ARCHITECTURAL MAPPING:
 * This class maps directly to the Client Network Manager component in the architectural diagram and to the
 * CNetworkManager class in the class diagram.
 *
 * Created by Eirik Skogstad.
 */
public class NetworkManager extends IntentService implements Invokable {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public NetworkManager(String name) {
        super(name);
    }

    /**
     * Called when the class receives an intent through a BroadcastReceiver
     *
     * @param intent
     */
    @Override
    public void onReceiveIntent(Intent intent) {

    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               android.content.Context#startService(android.content.Intent)}.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        this.onReceiveIntent(intent);
    }

    /**
     * Connect to the server.
     */
    private void connect() {

    }

    /**
     * Terminate the server connection.
     */
    private void disconnect() {

    }
}
