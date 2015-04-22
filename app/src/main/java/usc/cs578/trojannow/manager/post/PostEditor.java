package usc.cs578.trojannow.manager.post;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import usc.cs578.com.trojannow.R;
import usc.cs578.trojannow.manager.network.Method;

/*
 * Created by Ekasit_Ja on 19-Apr-15.
 */
public class PostEditor extends ActionBarActivity {

    private static final String TAG = PostEditor.class.getSimpleName();

    private boolean selectName = false;
    private boolean selectLocation = false;
    private boolean selectThermometer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_editor);

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
            finish();
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
                    case Method.getPostsByLocation: {
                        String jsonString = intent.getStringExtra(Method.resultKey);
                        /*Post[] posts = convertToPosts(jsonString);
                        populateListView(posts);*/
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

    public void toggleLocation(View v) {
        selectLocation = !selectLocation;
        ImageButton imgBtn = (ImageButton) v.findViewById(R.id.location_button);
        TextView location_label = (TextView) findViewById(R.id.location_label);
        TextView prefix_location = (TextView) findViewById(R.id.prefix_location_label);
        if(selectLocation) {
            imgBtn.setImageResource(R.mipmap.ic_location_selected);
            location_label.setVisibility(View.VISIBLE);
            prefix_location.setVisibility(View.VISIBLE);
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
        String temptSuffix = getString(R.string.fahrenheit_suffix);
        String tempt = String.format(getString(R.string.default_thermometer_label),temptSuffix);
        thermometer_label.setText(tempt);

        if(selectThermometer) {
            imgBtn.setImageResource(R.mipmap.ic_thermometer_selected);
            thermometer_label.setVisibility(View.VISIBLE);
        }
        else {
            imgBtn.setImageResource(R.mipmap.ic_thermometer);
            thermometer_label.setVisibility(View.GONE);
        }
        manageText();
    }

    public void toggleName(View v) {
        selectName = !selectName;
        ImageButton imgBtn = (ImageButton) v.findViewById(R.id.name_button);
        TextView name_label = (TextView) findViewById(R.id.name_label);
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

}
