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
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import usc.cs578.com.trojannow.R;
import usc.cs578.trojannow.manager.network.Method;
import usc.cs578.trojannow.manager.network.NetworkManager;

/*
 * Created by Ekasit_Ja on 14-Apr-15.
 */
public class CommentViewer extends ActionBarActivity {

    private static final String TAG = "PostEditor";
    private Post post;
    private Comment[] comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_viewer);

        // initiate and customize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(getString(R.string.comment_viewer_toolbar_title));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentViewer.this.finish();
            }
        });

        // register this view to receive intent name "PostViewer"
        LocalBroadcastManager.getInstance(this).registerReceiver(intentReceiver,
                new IntentFilter(TAG));

        // add listener to input field to inform user when text is too long
        TextView commentField = (TextView) findViewById(R.id.commenting_text);
        commentField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // warn user that they hit the maximum length
                int maxLength = getResources().getInteger(R.integer.comment_text_length);
                if(s.length() == maxLength) {
                    String warnedText = String.format(getString(R.string.warn_comment_length),maxLength);
                    Toast.makeText(CommentViewer.this, warnedText, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        } );

        // request post and its comments
        requestPostAndComments();
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
                    case Method.getPostAndComments: {
                        String jsonString = intent.getStringExtra("result");
                        post = convertToPost(jsonString);
                        comments = convertToComments(jsonString);
                        populateListView(post, comments);
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

    public void requestPostAndComments() {
        // request NetworkManager component to get data from server
        Intent intent = new Intent(this, NetworkManager.class);
        intent.putExtra("method", Method.getPostAndComments);
        intent.putExtra("postId", getIntent().getExtras().getInt("postId"));
        startService(intent);
    }

    private Post convertToPost(String jsonString) {
        Post post = null;
        try {
            // convert JSON string to JSON array
            JSONObject jObj = new JSONObject(jsonString).getJSONObject("post");
            // construct post
            post = new Post(
                jObj.getInt("id"),
                jObj.getString("poster_name"),
                jObj.getString("post_text"),
                jObj.getString("location"),
                jObj.getString("post_timestamp"),
                jObj.getInt("post_score"));
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON array " + e.toString());
        }
        return post;
    }

    private Comment[] convertToComments(String jsonString) {
        Comment[] comments = null;
        try {
            // convert JSON string to JSON array
            JSONArray jArr = new JSONObject(jsonString).getJSONArray("comment");
            comments = new Comment[jArr.length()];

            // construct element of posts
            for(int i=0; i<jArr.length(); i++) {
                JSONObject jObj = jArr.getJSONObject(i);
                comments[i] = new Comment(
                    jObj.getInt("id"),
                    jObj.getInt("post_id"),
                    jObj.getString("commentator_name"),
                    jObj.getString("comment_text"),
                    jObj.getString("comment_timestamp"),
                    jObj.getInt("comment_score"));
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON array " + e.toString());
        }
        return comments;
    }

    public void populateListView(Post post, Comment[] comments) {
        // create ArrayAdapter to manage data on the view
        CommentViewerAdapter adapter = new CommentViewerAdapter(this, post, comments);

        // bind the adapter to ListView
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        // add listener to the adapter
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),
                        "Click " + PostEditor.this.comments[position].id,
                        Toast.LENGTH_SHORT).show();
            }
        });*/

    }
}
