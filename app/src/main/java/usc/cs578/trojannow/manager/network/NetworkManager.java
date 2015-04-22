package usc.cs578.trojannow.manager.network;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import usc.cs578.trojannow.manager.post.CommentViewer;
import usc.cs578.trojannow.manager.post.PostViewer;
import usc.cs578.trojannow.manager.user.Register;

/*
 * Created by Ekasit_Ja on 13-Apr-15.
 */
public class NetworkManager extends IntentService {

    private static final String TAG = NetworkManager.class.getSimpleName();

    public NetworkManager() {
        super("NetworkManager");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // get parameters from intent to identify request
        String methodName = intent.getExtras().getString(Method.methodKey);
        if(methodName != null) {
            String url;
            Intent callbackIntent;

            switch (methodName) {
                case Method.getPostsByLocation: {
                    // get parameters, set URL, and create callback intent
                    String location = intent.getExtras().getString(Method.locationKey);
                    url = String.format(Url.getPostsByLocationApi, Uri.encode(location));
                    callbackIntent = new Intent(PostViewer.class.getSimpleName());
                    callbackIntent.putExtra(Method.methodKey, methodName);

                    // send intent back to caller
                    sendIntent(url, callbackIntent, Url.GET, "");
                    break;
                }
                case Method.getPostAndComments: {
                    String postId = String.valueOf(intent.getExtras().getInt(Method.postIdKey));
                    url = String.format(Url.getPostAndCommentsApi, Uri.encode(postId));
                    callbackIntent = new Intent(CommentViewer.class.getSimpleName());
                    callbackIntent.putExtra(Method.methodKey, methodName);
                    sendIntent(url, callbackIntent, Url.GET, "");
                    break;
                }
                case Method.registerUser: {
                    String postParameter = intent.getExtras().getString(Method.parameterKey);
                    url = Url.registerUser;
                    callbackIntent = new Intent(Register.class.getSimpleName());
                    callbackIntent.putExtra(Method.methodKey, methodName);
                    sendIntent(url, callbackIntent, Url.POST, postParameter);
                    break;
                }
                default: {
                    Log.w(TAG, "method switch falls default case");
                }
            }
        }
        else {
            Log.w(TAG, "receive intend without method name");
        }
    }

    private void sendIntent(String url, Intent callbackIntent, String httpMethod, String postParameter) {
        String jsonString;

        try {
            // make a request to get response
            if(httpMethod.equals(Url.GET)) {
                jsonString = RestCaller.callServer(url, Url.GET, "");
            }
            else {
                postParameter += Url.postSeparator+Url.macAddressKey+Url.postAssigner+getMacAddress(this);
                jsonString = RestCaller.callServer(url, Url.POST, postParameter);
            }

            // set value of intent
            callbackIntent.putExtra(Method.statusKey, true);
            callbackIntent.putExtra(Method.resultKey, jsonString);
        }
        catch (Exception e) {
            Log.e(TAG, "Error calling restGetMethod" + e.toString());
            // set value of intent
            callbackIntent.putExtra(Method.statusKey, false);
        }

        // send intent to caller
        LocalBroadcastManager.getInstance(this).sendBroadcast(callbackIntent);
    }

    private String getMacAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo().getMacAddress();
    }

}
