package usc.cs578.trojannow.manager.network;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/*
 * Created by Ekasit_Ja on 13-Apr-15.
 */
public class NetworkManager extends IntentService {

    private static final String TAG = "NetworkManager";

    public NetworkManager() {
        super("NetworkManager");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // get parameters from intent to identify request
        String method = intent.getExtras().getString("method");
        if(method != null) {
            String url;
            Intent callbackIntent;

            switch (method) {
                case Method.getPostsByLocation: {
                    // get parameters, set URL, and create callback intent
                    String location = intent.getExtras().getString("location");
                    url = String.format(Url.getPostsByLocationApi, Uri.encode(location));
                    callbackIntent = new Intent("PostViewer");

                    // send intent back to caller
                    sendIntent(url, callbackIntent, method);
                    break;
                }
                case Method.getPostAndComments: {
                    String postId = String.valueOf(intent.getExtras().getInt("postId"));
                    url = String.format(Url.getPostAndCommentsApi, Uri.encode(postId));
                    callbackIntent = new Intent("PostEditor");
                    sendIntent(url, callbackIntent, method);
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

    private void sendIntent(String url, Intent intent, String method) {
        String jsonString;
        intent.putExtra("method", method);

        try {
            // make a request to get response
            jsonString = new RestCaller(url).callGetMethod();

            // set value of intent
            intent.putExtra("status", true);
            intent.putExtra("result", jsonString);
        }
        catch (Exception e) {
            Log.e(TAG, "Error calling restGetMethod" + e.toString());
            // set value of intent
            intent.putExtra("status", false);
        }

        // send intent to caller
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
