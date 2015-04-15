package trojanow.storage;

import android.app.IntentService;
import android.content.Intent;

/**
 * PURPOSE:
 * This class allows other components to store and retrieve data in the system.
 *
 * OPERATION:
 * A component can store data by sending an intent with the data object to be stored,
 * and the Storage class will store the data if it adheres to a known format (e.g.
 * user information). Components can also request data from the Storage class by sending
 * intents to retrieve data.
 *
 * This class will have a BroadcastReceiver for incoming Intents and broadcast outgoing
 * Intents via the LocalBroadcastManager.
 *
 * ARCHITECTURAL MAPPING:
 * This class maps directly to the Client Storage component in the architectural diagram,
 * and to the ClientStorage class in the class diagram.
 *
 * Created by Eirik Skogstad.
 */
public class Storage extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public Storage(String name) {
        super(name);
    }

    /**
     * This function receives intents from other components,
     * and takes appropriate action based on the intent message.
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {

    }

    /**
     * Store an object in the database.
     */
    private void storeObject() {

    }

    /**
     * Retrieve an object from the database.
     */
    private void retrieveObject() {

    }
}
