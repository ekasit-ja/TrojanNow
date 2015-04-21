package usc.cs578.trojannow.manager.user;

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

import java.util.ArrayList;

import usc.cs578.com.trojannow.R;
import usc.cs578.trojannow.manager.network.Method;

/*
 * Created by Ekasit_Ja on 17-Apr-15.
 */
public class FriendViewer extends ActionBarActivity {

    private static final String TAG = "Friends";

    private ListView friendList;
    private ArrayList<Friend> friends;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends);

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

        // request list of our friends
        requestFriendList();
    }

    private void requestFriendList() {
        friends = new ArrayList<>();
        friends.add(new Friend(0,"Ake"));
        friends.add(new Friend(1,"Boom"));
        friends.add(new Friend(2,"Nic"));
        friends.add(new Friend(3,"John"));
        friends.add(new Friend(4,"High"));

        populateListView();
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

}
