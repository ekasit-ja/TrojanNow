package usc.cs578.trojannow.manager.chat;

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
import java.util.HashMap;
import java.util.List;

import usc.cs578.com.trojannow.R;
import usc.cs578.trojannow.intents.trojannowIntents;
import usc.cs578.trojannow.manager.network.Method;
import usc.cs578.trojannow.manager.network.NetworkManager;
import usc.cs578.trojannow.manager.network.Url;
import usc.cs578.trojannow.manager.user.Friend;

/**
 * PURPOSE:
 * This class will implement the functionality required to let users send instant messages to their
 * friends.
 *
 * OPERATION:
 * This class is responsible for displaying the chat screen and handling user
 * input on this screen. The screen will allow the user to select a friend to chat with,
 * see chat messages and write chat messages.
 *
 * This class will contain a BroadcastReceiver which will register to the LocalBroadcastManager
 * to receive chat messages from other users. Chat messages from the current user will be distributed
 * by the LocalBroadcastManager to other components.
 *
 * Since all chat communication goes through the server, the NetworkManager will be responsible
 * for passing messages to the server and distributing the messages coming from the server.
 *
 * ARCHITECTURAL MAPPING:
 * The Chat class maps directly to the Chat Client component in the architectural diagram and the
 * ChatClient class in the class diagram.
 *
 */

public class Chat extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

	private static final String TAG = Chat.class.getSimpleName();

	private static boolean activityVisible;
    private ArrayList<Friend> friends;
    //HashMap<Integer, String> friendsList;
    HashMap<Integer, List> chatHistory;
	private boolean isFromNotification = false;
	private int from_user_id = -1;
	private boolean isFirstLine = true;

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
						JSONObject jObj = new JSONObject(jsonString);
						String from_user = jObj.getString("from_user");
						String to_user = jObj.getString("to_user");
						int max_id = -1;
						int min_id = -1;

                        JSONArray jArr = jObj.getJSONArray("messages");
                        for(int i=0; i<jArr.length(); i++) {
                            JSONObject messageObj = jArr.getJSONObject(i);
                            String friend = getActiveFriend();
                            String message = messageObj.getString("message");
                            int friendId = getFriendByName(friend);

							// determine boundary of message id that is being read
							int msg_id = messageObj.getInt("id");
							if(msg_id >= max_id) max_id = msg_id;
							if(msg_id <= min_id) min_id = msg_id ;

                            addMessage(friend+": "+message);
                            chatHistory.get(friendId).add(friend+": "+message);
                        }

						// send mark read to server
						Intent markReadIntent = new Intent(Chat.this, NetworkManager.class);
						markReadIntent.putExtra(Method.methodKey, Method.sendHasSeen);
						markReadIntent.putExtra("from_user",from_user);
						markReadIntent.putExtra("to_user",to_user);
						markReadIntent.putExtra("max_id",max_id+"");
						markReadIntent.putExtra("min_id",min_id+"");
						startService(markReadIntent);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
				case Method.autoLoadNewMessage: {
					getUnreadMessages();
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

		activityResumed();

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
		LocalBroadcastManager.getInstance(this).registerReceiver(intentReceiver,
				new IntentFilter(Method.autoLoadNewMessage));
		LocalBroadcastManager.getInstance(this).registerReceiver(intentReceiver,
				new IntentFilter(Method.chatFromNotification));

        // Init the spinner event listener
        Spinner spinner = (Spinner) findViewById(R.id.friends_spinner);
        spinner.setOnItemSelectedListener(this);

        chatHistory = new HashMap<>();

        getFriends();

		// check if start activity by notification
		Intent i = getIntent();
		int from_user = i.getIntExtra(Method.fromUserKey, -1);
		if(from_user > -1) {
			isFromNotification = true;
			from_user_id = from_user;
		}
    }

	@Override
	public void onResume() {
		super.onResume();
		activityResumed();
	}

	@Override
	public void onPause() {
		super.onPause();
		activityPaused();
	}

    @Override
    protected void onDestroy() {
		activityPaused();

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

		if(isFromNotification) {
			int position = -1;
			for(int i=0; i<friends.size(); i++) {
				if(friends.get(i).getId() == from_user_id) {
					position = i;
					break;
				}
			}

			if(position > -1) {
				spinner.setSelection(position);
				getUnreadMessages();
			}
			else {
				Log.e(TAG, "got messages from unknown");
			}

			isFromNotification = false;
			from_user_id = -1;
		}
    }

    public void addMessage(String message) {
        TextView chatWindow = (TextView) findViewById(R.id.textView);
        final ScrollView scroll = (ScrollView) findViewById(R.id.scrollView);

		if(isFirstLine) {
			chatWindow.append(message);
			isFirstLine = false;
		}
		else {
			chatWindow.append('\n' + message);
		}
        scroll.post(new Runnable() {
			@Override
			public void run() {
				scroll.fullScroll(View.FOCUS_DOWN);
			}
		});
    }


    public void sendMessage(View view) {
        // Get the message and name of the friend
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
		editText.setText("");
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

    public void getUnreadMessages() {
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

        //String friend = getActiveFriend();
        //int friendId = getFriendByName(friend);
		int friendId = friends.get(position).getId();


        for (Object message : chatHistory.get(friendId)) {
            addMessage((String) message);
        }

		getUnreadMessages();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

	public static boolean isActivityVisible() {
		return activityVisible;
	}

	public static void activityResumed() {
		activityVisible = true;
	}

	public static void activityPaused() {
		activityVisible = false;
	}
}
