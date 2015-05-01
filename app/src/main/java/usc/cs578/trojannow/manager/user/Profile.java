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
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import usc.cs578.com.trojannow.R;
import usc.cs578.trojannow.manager.network.Method;
import usc.cs578.trojannow.manager.network.NetworkManager;

/**
 * PURPOSE:
 * This class allows the user to view other users profiles.
 *
 * OPERATION:
 * This class displays a screen where the user can search for another user
 * and view her profile.
 *
 * ARCHITECTURAL MAPPING:
 * This class is part of the Profile component in the architectural diagram and maps directly
 * to the ProfileViewer class in the class diagram.
 *
 * Created by echo on 3/29/15.
 */

public class Profile extends ActionBarActivity {
    private static final String TAG = Profile.class.getSimpleName();

    public static final String name = "display_name";
    public static final String age = "age";
    public static final String school = "school";
    public static final String gradYear = "grad_year";
    public static final String about = "about";

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

                        pAge = profileData.getInt(age);
                        pSchool = profileData.getString(school);
                        pGradYear = profileData.getInt(gradYear);
                        pAbout = profileData.getString(about);
                        pDisplayName = profileData.getString(name);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

					String undefined = "undefined";

                    TextView tName = (TextView) findViewById(R.id.profile_name);
                    tName.setText(pDisplayName);

					TextView tAge = (TextView) findViewById(R.id.profile_age);
                    if(pAge > 0) {
						tAge.setText(String.valueOf(pAge));
						tAge.setTextColor(getResources().getColor(R.color.black));
					}
					else {
						tAge.setText(undefined);
						tAge.setTextColor(getResources().getColor(R.color.grey));
					}

					TextView tSchool = (TextView) findViewById(R.id.profile_school);
					if(pSchool.length() > 0) {
						tSchool.setText(pSchool);
						tSchool.setTextColor(getResources().getColor(R.color.black));
					}
					else {
						tSchool.setText(undefined);
						tSchool.setTextColor(getResources().getColor(R.color.grey));
					}

					TextView tGradYear = (TextView) findViewById(R.id.profile_gradyear);
					if(pGradYear > 0) {
						tGradYear.setText(String.valueOf(pGradYear));
						tGradYear.setTextColor(getResources().getColor(R.color.black));
					}
					else {
						tGradYear.setText(undefined);
						tGradYear.setTextColor(getResources().getColor(R.color.grey));
					}

					TextView tAbout = (TextView) findViewById(R.id.profile_about);
					if(pAbout.length() > 0) {
						tAbout.setText(pAbout);
						findViewById(R.id.label_about).setVisibility(View.VISIBLE);
						tAbout.setVisibility(View.VISIBLE);
					}
					else {
						findViewById(R.id.label_about).setVisibility(View.GONE);
						tAbout.setVisibility(View.GONE);
					}

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
        setContentView(R.layout.profile);

        // initiate and customize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile.this.finish();
            }
        });

        // register this view to receive intent name "Profile"
        LocalBroadcastManager.getInstance(this).registerReceiver(intentReceiver,
                new IntentFilter(TAG));

        Intent intent = getIntent();
        String userId = intent.getExtras().get(Method.userIdKey).toString();

        if (userId.equals("active")) {
            enableEdit(true);
        } else {
            enableEdit(false);
        }

        getProfileData(userId);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		String userId = getIntent().getExtras().get(Method.userIdKey).toString();
		getProfileData(userId);

		setIntent(intent);
	}

    private void enableEdit(boolean edit) {
        Button editBtn = (Button) findViewById(R.id.profile_edit);
        if (edit) {
            editBtn.setVisibility(View.VISIBLE);
        } else {
            editBtn.setVisibility(View.GONE);
        }
    }

    private void getProfileData(String userId) {
        Intent intent = new Intent(this, NetworkManager.class);
        intent.putExtra(Method.TAG, TAG);
        intent.putExtra(Method.methodKey, Method.getProfileData);
        intent.putExtra(Method.userIdKey, userId);
        startService(intent);
    }

    public void onEditBtnClick(View view) {
        Intent intent = new Intent(this, ProfileEditor.class);
        startActivity(intent);
    }
}
