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
import android.widget.TextView;
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
public class Login extends ActionBarActivity {

    private static final String TAG = Login.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // default password hint font is weird. set it normal
        EditText password = (EditText) findViewById(R.id.password);
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());

        // initiate and customize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(getString(R.string.login_toolbar_title));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login.this.finish();
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
                    case Method.login: {
                        String jsonString = intent.getStringExtra(Method.resultKey);
                        handleLoginResponse(jsonString);
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

    private void handleLoginResponse(String jsonString) {
        try {
            JSONObject jObj = new JSONObject(jsonString);
            if(jObj.getBoolean(Url.statusKey)) {

                //save android registration id into server
                if (NetworkManager.checkPlayServices(Login.this)) {
                    NetworkManager.registerInBackground(Login.this);
                }

                // request post viewer to refresh page
                Intent intent = new Intent(this, PostViewer.class);
                intent.putExtra(Method.methodKey, Method.loginSuccess);
                startActivity(intent);

                // terminate self
                finish();
            }
            else {
                String toastText = jObj.getString(Url.errorMsgKey);
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON data");
        }
    }

    public void goToHelpPassword(View v) {
        Intent intent = new Intent(this, ForgotPassword.class);
        startActivity(intent);
    }

    public void goToRegister(View v) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    public void doLogin(View v) {
        String email = ((TextView) findViewById(R.id.email)).getText().toString().trim();
        String password = ((TextView) findViewById(R.id.password)).getText().toString().trim();

        String parameter = Url.emailKey+Url.postAssigner+email+Url.postSeparator;
        parameter += Url.passwordKey+Url.postAssigner+password+Url.postSeparator;

        // request NetworkManager component to register new user
        Intent intent = new Intent(this, NetworkManager.class);
        intent.putExtra(Method.methodKey, Method.login);
        intent.putExtra(Method.parameterKey, parameter);
        startService(intent);
    }

}
