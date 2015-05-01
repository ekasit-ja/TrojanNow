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
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;

import usc.cs578.trojannow.intents.trojannowIntents;
import usc.cs578.trojannow.manager.network.Method;
import usc.cs578.trojannow.manager.network.NetworkManager;
import usc.cs578.trojannow.manager.post.PostEditor;

/**
 * Created by echo on 5/1/15.
 */
public class tnSensorManager extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

	private static final String TAG = tnSensorManager.class.getSimpleName();

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
				case Method.getCityFromGPS: {
					String caller = intent.getStringExtra(Method.callerKey);
					getCityNameFromGPS(caller);
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
        } else {
            String temp = null;
            try {
                temp = getTempFromWeb("Los Angeles");
            } catch (Exception e) {
                e.printStackTrace();
            }

            float temperature = Float.parseFloat(temp);

            Intent intent = new Intent(trojannowIntents.temperature);
            intent.putExtra("value", temperature);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
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

    private String getAddress() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(lat, lng, 1);

            if (addresses.size() > 0) {
                return addresses.get(0).getLocality();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private String  getTempFromWeb(String string) throws Exception {
        string = string.replace(" ", "%20");

        URL url = null;
        URLConnection conec = null;
        InputStream stream = null;
        XmlPullParser xpp = null;

        String queryString = "http://api.wunderground.com/auto/wui/geo/WXCurrentObXML/index.xml?query=" + string;
        try {
            url = new URL(queryString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        conec = url.openConnection();
        stream = conec.getInputStream();
        try {
            xpp = XmlPullParserFactory.newInstance().newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        xpp.setInput(stream, null);
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String elementName = xpp.getName();
                if ("temp_c".equals(elementName)) {
                    return xpp.nextText();
                }
            }
            eventType = xpp.next();
        }

        return "";
    }

    public void getCityNameFromGPS(String caller) {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();

		Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
		List<Address> addresses;
		try {
			addresses = gcd.getFromLocation(latitude, longitude, 1);
			if (addresses.size() > 0) {
				JSONObject jObj = new JSONObject();
				try {
					jObj.put(Method.statusKey, true);
					jObj.put(Method.latitudeKey, latitude);
					jObj.put(Method.longitudeKey, longitude);
					jObj.put(Method.cityNameKey, addresses.get(0).getLocality());
				}
				catch(JSONException e) {
					Log.e(TAG, "Error creating JSON object "+e.toString());
				}
				Intent callbackIntent = new Intent(caller);
				callbackIntent.putExtra(Method.statusKey,true);
				callbackIntent.putExtra(Method.methodKey, Method.getCityFromGPS);
				callbackIntent.putExtra(Method.resultKey, jObj.toString());
				LocalBroadcastManager.getInstance(this).sendBroadcast(callbackIntent);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
