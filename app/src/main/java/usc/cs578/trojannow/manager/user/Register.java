package usc.cs578.trojannow.manager.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import usc.cs578.com.trojannow.R;
import usc.cs578.trojannow.manager.network.Method;
import usc.cs578.trojannow.manager.network.NetworkManager;
import usc.cs578.trojannow.manager.network.Url;
import usc.cs578.trojannow.manager.post.PostViewer;

/*
 * Created by Ekasit_Ja on 17-Apr-15.
 */
public class Register extends ActionBarActivity {

    private static final String TAG = Register.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // default password hint font is weird. set it normal
        EditText password = (EditText) findViewById(R.id.password);
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());
        EditText confirmPassword = (EditText) findViewById(R.id.confirm_password);
        confirmPassword.setTypeface(Typeface.DEFAULT);
        confirmPassword.setTransformationMethod(new PasswordTransformationMethod());

        // initiate and customize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(getString(R.string.register_toolbar_title));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register.this.finish();
            }
        });

        // register this view to receive intent name "PostViewer"
        LocalBroadcastManager.getInstance(this).registerReceiver(intentReceiver,
                new IntentFilter(TAG));
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(intentReceiver);
        super.onDestroy();
    }

    // handler for callback intent from NetworkManager component
    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if(intent.getBooleanExtra(Method.statusKey, false)) {
                String method = intent.getStringExtra(Method.methodKey);
                switch (method) {
                    case Method.registerUser: {
                        String jsonString = intent.getStringExtra(Method.resultKey);
                        displayResult(jsonString);
                        break;
                    }
                    case Method.loginAfterRegister: {
                        String jsonString = intent.getStringExtra(Method.resultKey);
                        try {
                            // convert JSON string to JSON object
                            JSONObject jObj = new JSONObject(jsonString);

                            if(jObj.getBoolean(Url.statusKey)) {
                                // directly go to post viewer and close login page automatically
                                intent = new Intent(context, PostViewer.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra(Method.methodKey, Method.registerSuccess);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(context,
                                        jObj.getString(Url.errorMsgKey),
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing JSON array " + e.toString());
                        }
                        break;
                    }
                    default: {
                        Log.w(TAG, "receive method switch case default");
                    }
                }
            }
            else {
                Log.e(TAG, "NetworkManager reply status FALSE");
            }
        }
    };

    private void displayResult(String jsonString) {
        String toastText = "";
        try {
            // convert JSON string to JSON object
            JSONObject jObj = new JSONObject(jsonString);

            if(jObj.getBoolean(Url.statusKey)) {
                toastText = "Signing up is successful";

                // auto login
                doLogin();
            }
            else {
                toastText = jObj.getString(Url.errorMsgKey);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON array " + e.toString());
        }

        Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
    }

    public void doRegister(View v) {
        String email = ((EditText) findViewById(R.id.email)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();
        String confirm_password = ((EditText) findViewById(R.id.confirm_password)).getText().toString();
        String display_name = ((EditText) findViewById(R.id.display_name)).getText().toString();

        if(!password.equals(confirm_password)) {
            Toast.makeText(this, getString(R.string.password_not_same),Toast.LENGTH_LONG).show();
            return;
        }

        String parameter = Url.emailKey+Url.postAssigner+email+Url.postSeparator;
        parameter += Url.passwordKey+Url.postAssigner+password+Url.postSeparator;
        parameter += Url.displayNameKey+Url.postAssigner+display_name;

        // request NetworkManager component to register new user
        Intent intent = new Intent(this, NetworkManager.class);
        intent.putExtra(Method.methodKey, Method.registerUser);
        intent.putExtra(Method.parameterKey, parameter);
        startService(intent);
    }

    public void doLogin() {
        String email = ((EditText) findViewById(R.id.email)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();

        String parameter = Url.emailKey+Url.postAssigner+email+Url.postSeparator;
        parameter += Url.passwordKey+Url.postAssigner+password+Url.postSeparator;

        // request NetworkManager component to login
        Intent intent = new Intent(this, NetworkManager.class);
        intent.putExtra(Method.methodKey, Method.login);
        intent.putExtra(Method.parameterKey, parameter);
        startService(intent);
    }

}
