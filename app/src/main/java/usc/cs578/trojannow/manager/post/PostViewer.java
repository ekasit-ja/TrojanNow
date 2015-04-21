package usc.cs578.trojannow.manager.post;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import usc.cs578.trojannow.drawer.DrawerMenu;
import usc.cs578.com.trojannow.R;
import usc.cs578.trojannow.manager.network.Method;
import usc.cs578.trojannow.manager.network.NetworkManager;


public class PostViewer extends ActionBarActivity implements DrawerMenu.OnFragmentInteractionListener {

    private static final String TAG = "PostViewer";
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_viewer);

        // initiate and customize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(getResources().getString(R.string.post_viewer_toolbar_title));
        setSupportActionBar(toolbar);

        // set up drawer
        DrawerMenu drawer = (DrawerMenu) getSupportFragmentManager().findFragmentById(R.id.drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setUp(R.id.main_view, drawerLayout, toolbar);

        // register this view to receive intent name "PostViewer"
        LocalBroadcastManager.getInstance(this).registerReceiver(intentReceiver,
                new IntentFilter(TAG));

        // request posts by location at launch
        requestPostsByLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.option_menu_drawer) {
            drawerLayout.openDrawer(Gravity.END);
        }
        else if (id == R.id.option_pen) {
            Intent intent = new Intent(this, PostEditor.class);
            startActivity(intent);
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
                        Post[] posts = convertToPosts(jsonString);
                        populateListView(posts);
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

    private Post[] convertToPosts(String jsonString) {
        Post[] posts = null;
        try {
            // convert JSON string to JSON array
            JSONArray jArr = new JSONArray(jsonString);
            posts = new Post[jArr.length()];

            // construct element of posts
            for(int i=0; i<jArr.length(); i++) {
                JSONObject jObj = jArr.getJSONObject(i);
                posts[i] = new Post(
                    jObj.getInt("id"),
                    jObj.getString("poster_name"),
                    jObj.getString("post_text"),
                    jObj.getString("location"),
                    jObj.getString("post_timestamp"),
                    jObj.getInt("post_score"));
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON array " + e.toString());
        }
        return posts;
    }

    public void populateListView(Post[] posts) {
        // create ArrayAdapter to manage data on the view
        PostViewerAdapter adapter = new PostViewerAdapter(this,posts);

        // bind the adapter to ListView
        ListView listView = (ListView) findViewById(R.id.posts_list);
        listView.setAdapter(adapter);
    }

    public void requestPostsByLocation() {
        // request NetworkManager component to get data from server
        Intent intent = new Intent(this, NetworkManager.class);
        intent.putExtra("method", Method.getPostsByLocation);
        intent.putExtra("location", "Los Angeles");
        startService(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
