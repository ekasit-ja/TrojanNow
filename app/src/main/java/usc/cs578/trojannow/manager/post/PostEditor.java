package usc.cs578.trojannow.manager.post;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import usc.cs578.com.trojannow.R;
import usc.cs578.trojannow.manager.network.Method;

/*
 * Created by Ekasit_Ja on 19-Apr-15.
 */
public class PostEditor extends ActionBarActivity {

    private static final String TAG = "PostEditor";

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
            if(intent.getBooleanExtra("status", false)) {
                String method = intent.getStringExtra("method");
                switch (method) {
                    case Method.getPostsByLocation: {
                        String jsonString = intent.getStringExtra("result");
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
        TextView prefix_location = (TextView) findViewById(R.id.prefix_location);
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
    }

    public void toggleThermometer(View v) {
        selectThermometer = !selectThermometer;
        ImageButton imgBtn = (ImageButton) v.findViewById(R.id.thermometer_button);
        if(selectThermometer) {
            imgBtn.setImageResource(R.mipmap.ic_thermometer_selected);
        }
        else {
            imgBtn.setImageResource(R.mipmap.ic_thermometer);
        }
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
    }

}
