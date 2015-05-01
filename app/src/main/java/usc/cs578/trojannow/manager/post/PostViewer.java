package usc.cs578.trojannow.manager.post;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import usc.cs578.trojannow.drawer.DrawerMenu;
import usc.cs578.com.trojannow.R;
import usc.cs578.trojannow.manager.chat.Chat;
import usc.cs578.trojannow.manager.network.Method;
import usc.cs578.trojannow.manager.network.NetworkManager;
import usc.cs578.trojannow.manager.network.Url;

public class PostViewer extends ActionBarActivity implements DrawerMenu.OnFragmentInteractionListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = PostViewer.class.getSimpleName();
    private static final int spinnerShowTime = 1000;
    protected DrawerLayout drawerLayout;
	protected SwipeRefreshLayout swipeRefreshLayout;
	protected DrawerMenu drawer;
	protected Toolbar toolbar;
	protected SharedPreferences sharedPreferences;
	protected ArrayList<Post> posts = null;
	protected PostViewerAdapter adapter;

	// default variables when users log in
	protected String DISPLAY_NAME;
	protected int PREF_TEMPT_UNIT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_viewer);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_posts_list);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.red_viterbi, R.color.yellow_trojan);

        // get preferences tempt unit
        sharedPreferences = getSharedPreferences(Method.PREF_NAME, MODE_PRIVATE);
        PREF_TEMPT_UNIT = sharedPreferences.getInt(Method.TEMPT_UNITS, Method.FAHRENHEIT);
		String sessionId = sharedPreferences.getString(Url.sessionIdKey, "");
		if (sessionId != null && !sessionId.equals("")) {
			DISPLAY_NAME = sharedPreferences.getString(Url.displayNameKey, "");
		}

        // initiate and customize toolbar
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(getString(R.string.post_viewer_toolbar_title));
        setSupportActionBar(toolbar);

        // set up drawer
        drawer = (DrawerMenu) getSupportFragmentManager().findFragmentById(R.id.drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setUp(R.id.main_view, drawerLayout, toolbar);

        checkFromNotification();

        if (NetworkManager.checkPlayServices(this)) {
            // if there is no android registration id, create new one
            String regId = NetworkManager.getRegistrationId(this);
            if (regId.isEmpty()) {
                NetworkManager.registerInBackground(this);
            }

            // register this view to receive intent name "PostViewer"
            LocalBroadcastManager.getInstance(this).registerReceiver(intentReceiver,
                    new IntentFilter(TAG));

            // request posts by location at launch
            requestPostsByLocation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkManager.checkPlayServices(PostViewer.this);
    }

    private void checkFromNotification() {
        Intent i = getIntent();
        boolean fromNotification = i.getBooleanExtra(Method.fromNotificationKey, false);

        if(fromNotification) {
            switch(i.getStringExtra(Method.fromNotificationMethodKey)) {
                case Method.gotComment: {
                    int postId = getIntent().getIntExtra(Method.postIdKey, -1);
                    Intent intent = new Intent(this, CommentViewer.class);
                    intent.putExtra(Method.postIdKey, postId);
                    intent.putExtra(Method.scrollBottomKey, true);
                    startActivity(intent);
                    getIntent().removeExtra(Method.fromNotificationKey);
                    getIntent().removeExtra(Method.fromNotificationMethodKey);
                    getIntent().removeExtra(Method.postIdKey);
                    break;
                }
				case Method.gotChat: {
					int from_user = getIntent().getIntExtra(Method.fromUserKey, -1);
					Intent intent = new Intent(this, Chat.class);
					intent.putExtra(Method.fromUserKey, from_user);
					intent.putExtra(Method.scrollBottomKey, true);
					startActivity(intent);
					getIntent().removeExtra("from_user");
					getIntent().removeExtra(Method.scrollBottomKey);
					break;
				}
                default: {
                    Log.w(TAG, "checkFromNotification falls default case");
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        checkFromNotification();

        String methodValue = intent.getStringExtra(Method.methodKey);
        if(methodValue != null) {
            boolean doRefresh = false;

            // automatically refresh to page if login or register is success
            switch (methodValue) {
                case Method.loginSuccess: {
                    // rebuild drawer
                    doRefresh = true;
                    break;
                }
                case Method.registerSuccess: {
                    doRefresh = true;
                    break;
                }
                case Method.createPost: {
                    doRefresh = true;
                    break;
                }
                case Method.changeTemptUnit: {
                    doRefresh = true;
                    break;
                }
                default: {
                    Log.e(TAG, "onNewIntent falls default case");
                }
            }

            if (doRefresh) {
                drawer.setUp(R.id.main_view, drawerLayout, toolbar);
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        }
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
            if(intent.getBooleanExtra(Method.statusKey, false)) {
                String methodName = intent.getStringExtra(Method.methodKey);
				String jsonString = intent.getStringExtra(Method.resultKey);
                switch (methodName) {
                    case Method.getPostsByLocation: {
                        posts = convertToPosts(jsonString);
                        populateListView(posts);
                        break;
                    }
                    case Method.ratePost: {
                        handleRatePost(jsonString);
                        break;
                    }
                    case Method.refreshPostViewer: {
                        posts = convertToPosts(jsonString);
                        refreshListView(posts);
                        break;
                    }
					case Method.logout: {
						handleLogout(jsonString);
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

	private void handleLogout(String jsonString) {
		try {
			JSONObject jObj = new JSONObject(jsonString);
			if(jObj.getBoolean(Url.statusKey)) {
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.remove(Url.displayNameKey);
				editor.remove(Url.sessionIdKey);
				editor.apply();

				// refresh page
				drawer.setUp(R.id.main_view, drawerLayout, toolbar);
				swipeRefreshLayout.setRefreshing(true);
				onRefresh();
			}
			else {
				Toast.makeText(this,jObj.getString(Url.errorMsgKey),Toast.LENGTH_LONG).show();
			}
		} catch (JSONException e) {
			Log.e(TAG, "Error parsing JSON object: " + e.toString());
		}
	}

	private ArrayList<Post> convertToPosts(String jsonString) {
        posts = null;
        try {
            // convert JSON string to JSON array
            JSONArray jArr = new JSONArray(jsonString);
            posts = new ArrayList<>();

            // construct element of posts
            for(int i=0; i<jArr.length(); i++) {
                JSONObject jObj = jArr.getJSONObject(i);
                posts.add( new Post(
                    jObj.getInt("id"),
                    jObj.getString("display_name"),
                    jObj.getString("post_text"),
                    jObj.getString("location"),
                    jObj.getString("post_timestamp"),
                    jObj.getInt("post_score"),
                    jObj.getInt("reply_count"),
                    jObj.getInt("user_rating"),
                    jObj.getString("tempt_in_c")) );
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON array " + e.toString());
        }
        return posts;
    }

    public void populateListView(ArrayList<Post> posts) {
        // create ArrayAdapter to manage data on the view
        adapter = new PostViewerAdapter(this, posts, PREF_TEMPT_UNIT);

        // bind the adapter to ListView
        ListView listView = (ListView) findViewById(R.id.posts_list);
        listView.setAdapter(adapter);

        // end refreshing icon once list view is populated
        if(swipeRefreshLayout.isRefreshing()) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, spinnerShowTime);
        }
    }

    public void requestPostsByLocation() {
        // request NetworkManager component to get data from server
        Intent intent = new Intent(this, NetworkManager.class);
        intent.putExtra(Method.methodKey, Method.getPostsByLocation);
        intent.putExtra("location", "Los Angeles");
        startService(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onRefresh() {
        PREF_TEMPT_UNIT = sharedPreferences.getInt(Method.TEMPT_UNITS, Method.FAHRENHEIT);

        // reload all posts again
        requestPostsByLocation();
    }

    private void handleRatePost(String jsonString) {
        try {
            JSONObject jObj = new JSONObject(jsonString);
            if(!jObj.getBoolean(Url.statusKey)) {
                String toastText = jObj.getString(Url.errorMsgKey);
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
                adapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON array " + e.toString());
        }
    }

    private void refreshListView(ArrayList<Post> posts) {
        adapter.posts = posts;
        adapter.notifyDataSetChanged();
    }

}
