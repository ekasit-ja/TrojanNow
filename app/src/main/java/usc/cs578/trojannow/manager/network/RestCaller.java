package usc.cs578.trojannow.manager.network;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 * Created by Ekasit_Ja on 14-Apr-15.
 */
public class RestCaller {

    private String url;

    public RestCaller(String url) {
        this.url = url;
    }

    public String callGetMethod() throws Exception{
        Exception exception = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        BufferedReader reader = null;
        String jsonString = "";

        // establish http request and get response
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(10000 /* milliseconds */);
            conn.connect();
            is = new BufferedInputStream(conn.getInputStream());
        }
        catch (Exception e) {
            Log.e("Connection Error", "Error making http request " + e.toString());
            exception = e;
        }

        // convert response to String
        try {
            if(is != null) {
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line = reader.readLine();
                while (line != null) {
                    line += "\n";
                    sb.append(line);
                    line = reader.readLine();
                }
                jsonString = sb.toString();
            }
            else {
                throw new NullPointerException("InputStreamReader is NULL");
            }
        }
        catch (Exception e) {
            Log.e("Conversion Error", "Error converting input stream to String " + e.toString());
            exception = e;
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }

            // input stream reader is closed automatically when its buffer reader is closed
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    Log.e("Closure Error", "Error closing buffered reader " + e.toString());
                    exception = e;
                }
            }
        }

        if(exception != null) {
            throw exception;
        }

        return jsonString;
    }
}
