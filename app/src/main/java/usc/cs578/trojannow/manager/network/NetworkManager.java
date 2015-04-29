package usc.cs578.trojannow.manager.network;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import java.io.IOException;

import usc.cs578.trojannow.manager.post.CommentViewer;
import usc.cs578.trojannow.manager.post.PostEditor;
import usc.cs578.trojannow.manager.post.PostViewer;
import usc.cs578.trojannow.manager.user.ForgotPassword;
import usc.cs578.trojannow.manager.user.Login;
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
                case Method.forgotPassword: {
                    String email = String.valueOf(intent.getExtras().getString(Method.emailKey));
                    url = String.format(Url.forgotPassword, Uri.encode(email));
                    callbackIntent = new Intent(ForgotPassword.class.getSimpleName());
                    callbackIntent.putExtra(Method.methodKey, methodName);
                    sendIntent(url, callbackIntent, Url.GET, "");
                    break;
                }
                case Method.login: {
                    String postParameter = intent.getExtras().getString(Method.parameterKey);
                    url = Url.login;
                    callbackIntent = new Intent(Login.class.getSimpleName());
                    callbackIntent.putExtra(Method.methodKey, methodName);
                    sendIntent(url, callbackIntent, Url.POST, postParameter);
                    break;
                }
                case Method.loginAfterRegister: {
                    String postParameter = intent.getExtras().getString(Method.parameterKey);
                    url = Url.login;
                    callbackIntent = new Intent(Register.class.getSimpleName());
                    callbackIntent.putExtra(Method.methodKey, methodName);
                    sendIntent(url, callbackIntent, Url.POST, postParameter);
                    break;
                }
                case Method.createComment: {
                    String postParameter = intent.getExtras().getString(Method.parameterKey);
                    url = Url.createComment;
                    callbackIntent = new Intent(CommentViewer.class.getSimpleName());
                    callbackIntent.putExtra(Method.methodKey, methodName);
                    sendIntent(url, callbackIntent, Url.POST, postParameter);
                    break;
                }
                case Method.createPost: {
                    String postParameter = intent.getExtras().getString(Method.parameterKey);
                    url = Url.createPost;
                    callbackIntent = new Intent(PostEditor.class.getSimpleName());
                    callbackIntent.putExtra(Method.methodKey, methodName);
                    sendIntent(url, callbackIntent, Url.POST, postParameter);
                    break;
                }
                case Method.ratePost: {
                    String postParameter = intent.getExtras().getString(Method.parameterKey);
                    url = Url.ratePost;
                    callbackIntent = new Intent(PostViewer.class.getSimpleName());
                    callbackIntent.putExtra(Method.methodKey, methodName);
                    sendIntent(url, callbackIntent, Url.POST, postParameter);
                    break;
                }
                case Method.ratePostFromComment: {
                    String postParameter = intent.getExtras().getString(Method.parameterKey);
                    url = Url.ratePost;
                    callbackIntent = new Intent(CommentViewer.class.getSimpleName());
                    callbackIntent.putExtra(Method.methodKey, methodName);
                    sendIntent(url, callbackIntent, Url.POST, postParameter);
                    break;
                }
                case Method.rateComment: {
                    String postParameter = intent.getExtras().getString(Method.parameterKey);
                    url = Url.rateComment;
                    callbackIntent = new Intent(CommentViewer.class.getSimpleName());
                    callbackIntent.putExtra(Method.methodKey, methodName);
                    sendIntent(url, callbackIntent, Url.POST, postParameter);
                    break;
                }
                case Method.refreshCommentViewer: {
                    String postId = String.valueOf(intent.getExtras().getInt(Method.postIdKey));
                    url = String.format(Url.getPostAndCommentsApi, Uri.encode(postId));
                    callbackIntent = new Intent(CommentViewer.class.getSimpleName());
                    callbackIntent.putExtra(Method.methodKey, methodName);
                    sendIntent(url, callbackIntent, Url.GET, "");
                    break;
                }
                case Method.refreshPostViewer: {
                    String location = intent.getExtras().getString(Method.locationKey);
                    url = String.format(Url.getPostsByLocationApi, Uri.encode(location));
                    callbackIntent = new Intent(PostViewer.class.getSimpleName());
                    callbackIntent.putExtra(Method.methodKey, methodName);
                    sendIntent(url, callbackIntent, Url.GET, "");
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
                jsonString = new RestCaller().callServer(this, url, Url.GET, "");
            }
            else {
                jsonString = new RestCaller().callServer(this, url, Url.POST, postParameter);
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

    public static boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        Url.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(Method.PREF_NAME, MODE_PRIVATE);
        String registrationId = prefs.getString(Method.PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.w(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(Method.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static void registerInBackground(final Context context) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg;
                try {
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                    String regId = gcm.register(Url.SENDER_ID);
                    msg = "Device registered, registration ID=" + regId;

                    sendRegistrationIdToBackend(context, regId);
                    storeRegistrationId(context, regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }

                return msg;
            }

            @Override
            protected void onPostExecute(String s) {
                Log.e(TAG, s);
            }
        }.execute(null, null, null);
    }

    private static void sendRegistrationIdToBackend(Context context, String regId) {
        String postParameter = Url.androidRegistrationIdKey+Url.postAssigner+regId+Url.postSeparator;
        String url = Url.updateAndroidRegistrationId;
        Intent callbackIntent = new Intent(CommentViewer.class.getSimpleName());
        callbackIntent.putExtra(Method.methodKey, Method.updateAndroidRegistrationId);

        try {
            String jsonString = new RestCaller().callServer(context, url, Url.POST, postParameter);
            JSONObject jObj = new JSONObject(jsonString);
            //check if saving registration id is success or not
            if(jObj.getBoolean(Url.statusKey)) {
                Log.i(TAG, "Updating android registration id is success");
            }
            else {
                Log.e(TAG, jObj.getString(Url.errorMsgKey));
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Error calling restGetMethod" + e.toString());
        }
    }

    private static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = context.getSharedPreferences(Method.PREF_NAME, MODE_PRIVATE);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Method.PROPERTY_REG_ID, regId);
        editor.putInt(Method.PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

}
