package usc.cs578.trojannow.manager.user;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import usc.cs578.com.trojannow.R;
import usc.cs578.trojannow.intents.trojannowIntents;
import usc.cs578.trojannow.manager.network.Method;
import usc.cs578.trojannow.manager.network.NetworkManager;

/*
 * Created by Ekasit_Ja on 17-Apr-15.
 */
public class FriendViewer extends ActionBarActivity {

    private static Activity activity;

    private static final String TAG = Friend.class.getSimpleName();

    private ListView friendList;
    private ArrayList<Friend> friends;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends);

        activity = this;

        // initiate and customize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(getString(R.string.friends_toolbar_title));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendViewer.this.finish();
            }
        });

        // register this view to receive intent name "PostViewer"
        LocalBroadcastManager.getInstance(this).registerReceiver(intentReceiver,
                new IntentFilter(TAG));
        LocalBroadcastManager.getInstance(this).registerReceiver(intentReceiver,
                new IntentFilter(trojannowIntents.friendsList));

        // request list of our friends
        requestFriendList();
    }

    private void requestFriendList() {
        Intent intent = new Intent(this, NetworkManager.class);
        intent.putExtra(Method.methodKey, Method.getUsers);
        intent.putExtra(Method.usersKey, "all");
        startService(intent);


//        friends = new ArrayList<>();
//        friends.add(new Friend(12,"Ake", true));
//        friends.add(new Friend(33,"Boom", true));
//        friends.add(new Friend(22,"Nic", true));
//        friends.add(new Friend(4,"John", false));
//        friends.add(new Friend(5,"High", false));
//
//        populateListView();
    }

    private void populateListView() {
        // create ArrayAdapter to manage data on the view
        FriendViewerAdapter adapter = new FriendViewerAdapter(this,friends);

        // bind the adapter to ListView
        listView = (ListView) findViewById(R.id.friends_list);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friends_search, menu);

        // set listener of search on toolbar when text changed
        MenuItem searchItem = menu.findItem(R.id.search_toolbar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<Friend> temp = new ArrayList<>();
                int textLength = s.length();
                temp.clear();
                for(int i=0; i<friends.size(); i++) {
                    String name = friends.get(i).name;
                    if(textLength <= name.length()) {
                        if(name.toLowerCase().indexOf(s.toLowerCase()) >= 0) {
                            temp.add(friends.get(i));
                        }
                    }
                }
                FriendViewerAdapter adapter = new FriendViewerAdapter(FriendViewer.this,temp);
                listView.setAdapter(adapter);

                return false;
            }
        });

        // Configure the search info and add any event listeners

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;  // Return true to expand action view
            }
        });

        return super.onCreateOptionsMenu(menu);
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
                    case trojannowIntents.friendsList: {
                        String jsonString = intent.getStringExtra(Method.resultKey);
                        friends = new ArrayList<>();
                        Friend.getFriendsFromJSON(jsonString, friends);
                        populateListView();
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

    public static void restart() {
        activity.recreate();
    }
}
