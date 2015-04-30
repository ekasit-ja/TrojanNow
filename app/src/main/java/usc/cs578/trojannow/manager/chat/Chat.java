package usc.cs578.trojannow.manager.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import usc.cs578.com.trojannow.R;
import usc.cs578.trojannow.intents.trojannowIntents;
import usc.cs578.trojannow.manager.network.Method;
import usc.cs578.trojannow.manager.network.NetworkManager;
import usc.cs578.trojannow.manager.network.Url;
import usc.cs578.trojannow.manager.user.Friend;
import usc.cs578.trojannow.manager.user.Login;

/**
 * Created by echo on 4/22/15.
 */
public class Chat extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    private ArrayList<Friend> friends;
    //HashMap<Integer, String> friendsList;
    HashMap<Integer, List> chatHistory;

    // handler for callback intent from NetworkManager component
    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String action = intent.getAction();
            switch (action) {
                case trojannowIntents.friendsList: {
                    String jsonString = intent.getStringExtra("result");
                    friends = new ArrayList<>();
                    Friend.getFriendsFromJSON(jsonString, friends);
                    populateSpinner();

                    // Initialize the chat history
                    for (Friend friend : friends){
                        chatHistory.put(friend.getId(), new ArrayList());
                    }

                    break;
                }
                case trojannowIntents.chatMessages: {
                    String jsonString = intent.getStringExtra("result");
                    try {
                        JSONArray jArr = new JSONArray(jsonString);
                        for(int i=0; i<jArr.length(); i++) {
                            JSONObject messageObj = jArr.getJSONObject(i);
                            String friend = getActiveFriend();
                            String message = messageObj.getString("message");
                            int friendId = getFriendByName(friend);

                            addMessage(friend+": "+message);
                            chatHistory.get(friendId).add(friend+": "+message);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                default: {
                    Log.w("Chat", "receive method switch case default");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        // initiate and customize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Chat");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chat.this.finish();
            }
        });

        // register this view to receive intent
        LocalBroadcastManager.getInstance(this).registerReceiver(intentReceiver,
                new IntentFilter(trojannowIntents.friendsList));
        LocalBroadcastManager.getInstance(this).registerReceiver(intentReceiver,
                new IntentFilter(trojannowIntents.chatMessages));

        // Init the spinner event listener
        Spinner spinner = (Spinner) findViewById(R.id.friends_spinner);
        spinner.setOnItemSelectedListener(this);

        chatHistory = new HashMap<>();

        getFriends();
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(intentReceiver);
        super.onDestroy();
    }

    public void populateSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.friends_spinner);

        ArrayList<String> names = new ArrayList<>();
        for (Friend friend : friends) {
            names.add(friend.getName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, names);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void addMessage(String message) {
        TextView chatWindow = (TextView) findViewById(R.id.textView);
        ScrollView scroll = (ScrollView) findViewById(R.id.scrollView);

        chatWindow.append('\n'+message);
        scroll.fullScroll(View.FOCUS_DOWN);
    }


    public void sendMessage(View view) {
        // Get the message and name of the friend
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        Spinner spinner = (Spinner) findViewById(R.id.friends_spinner);
        String friend = spinner.getSelectedItem().toString();
        int friendId = getFriendByName(friend);

        // Add the message to the UI
        addMessage("You: " + message);
        chatHistory.get(friendId).add("You: " + message);

        // Send the intent to the server
        Intent intent = new Intent(this, NetworkManager.class);
        intent.putExtra(Method.methodKey, Method.sendMessage);
        intent.putExtra(Url.toUser, friendId);
        intent.putExtra(Url.message, message);
        startService(intent);
    }

    private void getFriends() {
        Intent intent = new Intent(this, NetworkManager.class);
        intent.putExtra(Method.methodKey, Method.getUsers);
        intent.putExtra(Method.usersKey, "friends");

        startService(intent);
    }

    private String getActiveFriend() {
        Spinner spinner = (Spinner) findViewById(R.id.friends_spinner);
        return spinner.getSelectedItem().toString();
    }

    /**
     * Returns the system id of a friend, e.g. the user id.
     *
     * For this to work, friends must be populated.
     *
     * TODO: Assumes that no users share the same name.
     *
     * @param name The name of the friend
     * @return Integer The user id or null if not found
     */
    private Integer getFriendByName(String name) {
        for (Friend friend : friends) {
            if (friend.getName().equals(name)) {
                return friend.getId();
            }
        }

        return null;
    }

    public void getUnreadMessages(View view) {
        int fromUser = getFriendByName(getActiveFriend());
        Intent intent = new Intent(this, NetworkManager.class);
        intent.putExtra("method", Method.getUnreadMessages);
        intent.putExtra(Url.fromUser, fromUser);
        startService(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView chatWindow = (TextView) findViewById(R.id.textView);
        chatWindow.setText("");

        String friend = getActiveFriend();
        int friendId = getFriendByName(friend);

        for (Object message : chatHistory.get(friendId)) {
            addMessage((String) message);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
