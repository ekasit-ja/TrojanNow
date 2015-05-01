package usc.cs578.trojannow.manager.post;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import usc.cs578.com.trojannow.R;
import usc.cs578.trojannow.intents.trojannowIntents;
import usc.cs578.trojannow.manager.network.Method;
import usc.cs578.trojannow.manager.network.NetworkManager;
import usc.cs578.trojannow.manager.network.Url;
import usc.cs578.trojannow.manager.sensor.tnSensorManager;

/**
 * PURPOSE:
 * This class enables the user to write posts and publish them.
 *
 * OPERATION:
 * Once a user submits a post, an Intent is created and broadcast through LocalBroadcastManager.
 * The NetworkManager will listen to such intents and send them to the server.
 *
 * This class will also receive Intents from the SensorManager when a user wants to include sensor data in the
 * post.
 *
 * ARCHITECTURAL MAPPING;
 * This class belongs to the Client Post Manager component in the architectural diagram, and maps directly to
 * the PostEditor class in the class diagram.
 *
 */

public class PostEditor extends ActionBarActivity {

    private static final String TAG = PostEditor.class.getSimpleName();

    private boolean selectName = false;
    private boolean selectLocation = false;
    private boolean selectThermometer = false;
    private String location = "";
    private String tempt_in_c = "";
    private int tempt_unit;
	protected String display_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_editor);

        // check tempt unit
        SharedPreferences sharedPreferences = getSharedPreferences(Method.PREF_NAME, MODE_PRIVATE);
        tempt_unit = sharedPreferences.getInt(Method.TEMPT_UNITS, Method.FAHRENHEIT);
		display_name = sharedPreferences.getString(Url.displayNameKey, "");
        String session_id = sharedPreferences.getString(Url.sessionIdKey, "");

        // remove name button if user doesn't login
        if(session_id.length() < 1) {
            ImageButton name_button = (ImageButton) findViewById(R.id.name_button);
            name_button.setVisibility(View.GONE);
        }

        // initiate and customize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(getResources().getString(R.string.post_editor_toolbar_title));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostEditor.this.finish();
            }
        });

        // add listener to inform user when post text hit the limit
        TextView postField = (TextView) findViewById(R.id.post_text);
        postField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // warn user that they hit the maximum length
                int maxLength = getResources().getInteger(R.integer.post_text_length);
                if(s.length() == maxLength) {
                    String warnedText = String.format(getString(R.string.warn_post_length),maxLength);
                    Toast.makeText(PostEditor.this, warnedText, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // register this view to receive intent name "PostViewer"
        LocalBroadcastManager.getInstance(this).registerReceiver(intentReceiver,
                new IntentFilter(TAG));
        LocalBroadcastManager.getInstance(this).registerReceiver(intentReceiver,
                new IntentFilter(trojannowIntents.temperature));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.option_post) {
            doPost();
        }

        return true;
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
                    case Method.createPost: {
                        String jsonString = intent.getStringExtra(Method.resultKey);
                        handleCreatePost(jsonString);
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

            if (intent.getAction().equals(trojannowIntents.temperature)) {
                setTemperature(Float.parseFloat(intent.getExtras().get("value").toString()));
            }
        }
    };

    public void toggleLocation(View v) {
        selectLocation = !selectLocation;
        ImageButton imgBtn = (ImageButton) v.findViewById(R.id.location_button);
        TextView location_label = (TextView) findViewById(R.id.location_label);
        TextView prefix_location = (TextView) findViewById(R.id.prefix_location_label);
        if(selectLocation) {
            imgBtn.setImageResource(R.mipmap.ic_location_selected);
            location_label.setVisibility(View.VISIBLE);
            prefix_location.setVisibility(View.VISIBLE);

            // get location here
            location = getString(R.string.default_location);
        }
        else {
            imgBtn.setImageResource(R.mipmap.ic_location);
            location_label.setVisibility(View.GONE);
            prefix_location.setVisibility(View.GONE);
        }
        manageText();
    }

    public void toggleThermometer(View v) {
        selectThermometer = !selectThermometer;
        ImageButton imgBtn = (ImageButton) v.findViewById(R.id.thermometer_button);
        TextView thermometer_label = (TextView) findViewById(R.id.thermometer_label);

        String temptSuffix;
        if(tempt_unit == Method.FAHRENHEIT) {
            temptSuffix = getString(R.string.fahrenheit_suffix);
        }
        else {
            temptSuffix = getString(R.string.celsius_suffix);
        }

        // get location here
        tempt_in_c = "25%s";
        tempt_in_c = String.format(tempt_in_c,temptSuffix);
        thermometer_label.setText(tempt_in_c);

        if(selectThermometer) {
            imgBtn.setImageResource(R.mipmap.ic_thermometer_selected);
            thermometer_label.setVisibility(View.VISIBLE);

            Intent intent = new Intent(this, tnSensorManager.class);
            intent.putExtra(Method.methodKey, Method.getTemperature);
            startService(intent);
        }
        else {
            imgBtn.setImageResource(R.mipmap.ic_thermometer);
            thermometer_label.setVisibility(View.GONE);
        }
        manageText();
    }

    public void setTemperature(float temp) {
        TextView thermometer_label = (TextView) findViewById(R.id.thermometer_label);

        String temptSuffix;
        if(tempt_unit == Method.FAHRENHEIT) {
            temp = ((temp - 32)*5)/9;
            temptSuffix = getString(R.string.fahrenheit_suffix);
        }
        else {
            temptSuffix = getString(R.string.celsius_suffix);
        }

        // get location here
        tempt_in_c = temp+"%s";
        tempt_in_c = String.format(tempt_in_c,temptSuffix);
        thermometer_label.setText(tempt_in_c);

        manageText();
    }

    public void toggleName(View v) {
        selectName = !selectName;
        ImageButton imgBtn = (ImageButton) v.findViewById(R.id.name_button);
        TextView name_label = (TextView) findViewById(R.id.name_label);
		name_label.setText(display_name);

        if(selectName) {
            imgBtn.setImageResource(R.mipmap.ic_name_selected);
            name_label.setVisibility(View.VISIBLE);
        }
        else {
            imgBtn.setImageResource(R.mipmap.ic_name);
            name_label.setVisibility(View.GONE);
        }
        manageText();
    }

    private void manageText() {
        TextView dash_label = (TextView) findViewById(R.id.dash_label);
        TextView prefix_location = (TextView) findViewById(R.id.prefix_location_label);
        //noinspection ConstantConditions
        if(!selectName || (selectName && !(selectLocation || selectThermometer))) {
            dash_label.setVisibility(View.GONE);
        }
        else {
            dash_label.setVisibility(View.VISIBLE);
        }

        if(selectLocation && !selectThermometer && !selectName) {
            String text = prefix_location.getText().toString();
            if(text.length() > 0) {
                text = text.substring(0,1).toUpperCase() + text.substring(1);
                prefix_location.setText(text);
            }
        }
        else {
            prefix_location.setText(getString(R.string.prefix_location_label));
        }
    }

    private void doPost() {
        String post_text = ((TextView)findViewById(R.id.post_text)).getText().toString();
        String tempt_in_c_digit;
        if(selectThermometer) {
            tempt_in_c_digit = tempt_in_c.substring(0, tempt_in_c.length() - 1);
        }
        else {
            tempt_in_c_digit = "";
        }

        // must have location info in the post
        if(location.length() < 1) {
            location = getString(R.string.default_location);
        }

        int show_name = selectName? 1: 0;
        int show_tempt = selectThermometer? 1: 0;
        int show_location = selectLocation? 1: 0;

        String parameter = Url.postTextKey+Url.postAssigner+post_text+Url.postSeparator;
        parameter += Url.showNameKey+Url.postAssigner+show_name+Url.postSeparator;
        parameter += Url.showLocationKey+Url.postAssigner+show_location+Url.postSeparator;
        parameter += Url.showTemptKey+Url.postAssigner+show_tempt+Url.postSeparator;
        parameter += Url.locationKey+Url.postAssigner+location+Url.postSeparator;
        parameter += Url.temptInCKey+Url.postAssigner+tempt_in_c_digit+Url.postSeparator;

        // request NetworkManager component to login
        Intent intent = new Intent(this, NetworkManager.class);
        intent.putExtra(Method.methodKey, Method.createPost);
        intent.putExtra(Method.parameterKey, parameter);
        startService(intent);
    }

    private void handleCreatePost(String jsonString) {
        try {
            // convert JSON string to JSON object
            JSONObject jObj = new JSONObject(jsonString);

            if(jObj.getBoolean(Url.statusKey)) {
                // tell post viewer to refresh
                // directly go to post viewer and close login page automatically
                Intent intent = new Intent(this, PostViewer.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(Method.methodKey, Method.createPost);
                startActivity(intent);
            }
            else {
                String toastText = jObj.getString(Url.errorMsgKey);
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON array " + e.toString());
        }

    }

}
