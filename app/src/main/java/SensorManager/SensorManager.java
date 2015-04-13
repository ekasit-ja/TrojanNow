package SensorManager;

import Interfaces.Invokable;
import android.app.IntentService;
import android.content.Intent;

/**
 * PURPOSE:
 * SensorManager provides a background service that can provide
 * data from sensors to other components.
 *
 * OPERATION:
 * A component can request for instance temperature or location data, and the SensorManager
 * will try to get the data from the respective sensor and send it back.
 *
 * Requests are received with a BroadcastReceiver and sensor data is broadcast via the
 * LocalBroadcastManager.
 *
 * ARCHITECTURAL MAPPING:
 * This class maps directly to the Sensor Manager component in the architectural diagram
 * and to the SensorManager class in the class diagram.
 *
 * Created by Eirik Skogstad.
 */
public class SensorManager extends IntentService implements Invokable {

    /**
     * Called when the class receives an intent through a BroadcastReceiver
     */
    @Override
    public void onReceiveIntent(Intent intent) {

    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SensorManager(String name) {
        super(name);
    }

    /**
     * This class can also be invoked directly since it is a background service.
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        this.onReceiveIntent(intent);
    }

    /**
     * Get data from a specific sensor,
     *
     * @param sensor The sensor to get data from.
     */
    private void getSensorData(Sensor sensor){

    }
}
