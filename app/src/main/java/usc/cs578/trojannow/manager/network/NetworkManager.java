package usc.cs578.trojannow.manager.network;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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

}
