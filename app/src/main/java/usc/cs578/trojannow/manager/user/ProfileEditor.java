package usc.cs578.trojannow.manager.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import usc.cs578.com.trojannow.R;
import usc.cs578.trojannow.manager.network.Method;
import usc.cs578.trojannow.manager.network.NetworkManager;

/**
 * Created by echo on 4/30/15.
 */
public class ProfileEditor extends ActionBarActivity {
    private static final String TAG = ProfileEditor.class.getSimpleName();

    // handler for callback intent from NetworkManager component
    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String method = intent.getStringExtra(Method.methodKey);
            switch (method) {
                case Method.profileData: {
                    String jsonString = intent.getStringExtra(Method.resultKey);

                    int pAge = 0, pGradYear = 0;
                    String pDisplayName = "", pSchool = "", pAbout = "";

                    try {
                        JSONObject jObj = new JSONObject(jsonString);
                        JSONObject profileData = jObj.getJSONObject("profile");

                        pAge = profileData.getInt(Profile.age);
                        pSchool = profileData.getString(Profile.school);
                        pGradYear = profileData.getInt(Profile.gradYear);
                        pAbout = profileData.getString(Profile.about);
                        pDisplayName = profileData.getString(Profile.name);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //EditText tName = (TextView) findViewById(R.id.profile_name);
                    //tName.setText(pDisplayName);
                    EditText eAge = (EditText) findViewById(R.id.edit_age);
                    eAge.setText(String.valueOf(pAge), TextView.BufferType.EDITABLE);
                    EditText eSchool = (EditText) findViewById(R.id.edit_school);
                    eSchool.setText(pSchool, TextView.BufferType.EDITABLE);
                    EditText eGradYear = (EditText) findViewById(R.id.edit_gradyr);
                    eGradYear.setText(String.valueOf(pGradYear), TextView.BufferType.EDITABLE);
                    EditText eAbout = (EditText) findViewById(R.id.edit_about);
                    eAbout.setText(pAbout, TextView.BufferType.EDITABLE);

                    break;
                }
                default: {
                    Log.w(TAG, "receive method switch case default");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_editor);

        // initiate and customize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Edit Profile");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileEditor.this.finish();
            }
        });

        // register this view to receive intent name "Profile"
        LocalBroadcastManager.getInstance(this).registerReceiver(intentReceiver,
                new IntentFilter(TAG));

        getProfileData("active");
    }

    private void getProfileData(String userId) {
        Intent intent = new Intent(this, NetworkManager.class);
        intent.putExtra(Method.TAG, TAG);
        intent.putExtra(Method.methodKey, Method.getProfileData);
        intent.putExtra(Method.userIdKey, userId);
        startService(intent);
    }

    public void saveProfileData(View view) {
        Intent intent = new Intent(this, NetworkManager.class);
        intent.putExtra(Method.methodKey, Method.updateProfile);

        EditText eAge = (EditText) findViewById(R.id.edit_age);
        EditText eSchool = (EditText) findViewById(R.id.edit_school);
        EditText eGradYear = (EditText) findViewById(R.id.edit_gradyr);
        EditText eAbout = (EditText) findViewById(R.id.edit_about);

        intent.putExtra(Profile.age, eAge.getText().toString());
        intent.putExtra(Profile.school, eSchool.getText().toString());
        intent.putExtra(Profile.gradYear, eGradYear.getText().toString());
        intent.putExtra(Profile.about, eAbout.getText().toString());

        startService(intent);

        intent = new Intent(this, Profile.class);
        intent.putExtra(Method.userIdKey, "active");
        startActivity(intent);
    }
}
