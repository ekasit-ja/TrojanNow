package usc.cs578.trojannow.manager.network;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/*
 * Created by Ekasit_Ja on 14-Apr-15.
 */
public class RestCaller {

    private static final int READ_TIMEOUT = 10000; // milliseconds
    private static final int CONNECT_TIMEOUT = 10000; // milliseconds

    public RestCaller() {
    }

    public static String callServer(String url, String httpMethod, String postParameter) throws Exception {
        Exception exception = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        BufferedReader reader = null;
        String jsonString = "";

        // establish http request and get response
        if(httpMethod.equals(Url.GET)) {
            try {
                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod(Url.GET);
                conn.setDoInput(true);
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                conn.connect();

                is = new BufferedInputStream(conn.getInputStream());
            }
            catch (Exception e) {
                Log.e("Connection Error", "Error making http request " + e.toString());
                exception = e;
            }
        }
        else {
            try {
                byte[] postData = postParameter.getBytes(Charset.forName("UTF-8"));
                int postDataLength = postData.length;
                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setInstanceFollowRedirects(false);
                conn.setRequestMethod(Url.POST);
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                conn.setRequestProperty( "charset", "utf-8");
                conn.setRequestProperty( "Content-Length", Integer.toString(postDataLength));
                conn.setUseCaches(false);

                // send request data
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.write(postData);
                wr.flush();
                wr.close();

                is = new BufferedInputStream(conn.getInputStream());
            }
            catch (Exception e) {
                Log.e("Connection Error", "Error making http request " + e.toString());
                exception = e;
            }
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
