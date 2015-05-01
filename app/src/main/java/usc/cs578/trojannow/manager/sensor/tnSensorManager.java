package usc.cs578.trojannow.manager.sensor;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import usc.cs578.trojannow.intents.trojannowIntents;
import usc.cs578.trojannow.manager.network.Method;

/**
 * Created by echo on 5/1/15.
 */
public class tnSensorManager extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    private SensorEventListener eventListener;

    public tnSensorManager() {
        super("tnSensorManager");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // get parameters from intent to identify request
        String methodName = intent.getExtras().getString(Method.methodKey);
        if(methodName != null) {

            switch (methodName) {
                case Method.getTemperature: {
                    getTemperature();
                    break;
                }
                case Method.getLocation: {
                    getLocation();
                    break;
                }
            }
        }
    }


    private void getTemperature() {
        final SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        if (tempSensor != null) {
            eventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    float temp = event.values[0];

                    Intent intent = new Intent(trojannowIntents.temperature);
                    intent.putExtra("value", temp);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                    sensorManager.unregisterListener(this);
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };

            sensorManager.registerListener(eventListener, tempSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(lat, lng, 1);

            if (addresses.size() > 0) {
                Intent intent = new Intent(trojannowIntents.location);
                intent.putExtra("value", addresses.get(0).getLocality());
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
