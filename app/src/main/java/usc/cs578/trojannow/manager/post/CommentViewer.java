package usc.cs578.trojannow.manager.post;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import usc.cs578.com.trojannow.R;
import usc.cs578.trojannow.manager.network.Method;
import usc.cs578.trojannow.manager.network.NetworkManager;
import usc.cs578.trojannow.manager.network.Url;

/*
 * Created by Ekasit_Ja on 14-Apr-15.
 */
public class CommentViewer extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = CommentViewer.class.getSimpleName();
    private static final int spinnerShowTime = 1000;

    protected SwipeRefreshLayout swipeRefreshLayout;
    protected Post post = null;
    protected ArrayList<Comment> comments = null;
    protected CommentViewerAdapter adapter = null;
    protected ListView listView = null;
    protected boolean customLauncher_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_viewer);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_posts_list);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.red_viterbi, R.color.yellow_trojan);

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
            if(intent.getBooleanExtra(Method.statusKey, false)) {
                String method = intent.getStringExtra(Method.methodKey);
                switch (method) {
                    case Method.getPostAndComments: {
                        String jsonString = intent.getStringExtra(Method.resultKey);
                        Post post = convertToPost(jsonString);
                        ArrayList<Comment> comments = convertToComments(jsonString);
                        populateListView(post, comments);
                        break;
                    }
                    case Method.createComment: {
                        String jsonString = intent.getStringExtra(Method.resultKey);
                        handleCreateComment(jsonString);
                        break;
                    }
                    case Method.ratePostFromComment: {
                        String jsonString = intent.getStringExtra(Method.resultKey);
                        handleRatePost(jsonString);
                        requestPostViewerRefresh();
                        break;
                    }
                    case Method.rateComment: {
                        String jsonString = intent.getStringExtra(Method.resultKey);
                        handleRateComment(jsonString);
                        break;
                    }
                    case Method.refreshCommentViewer: {
                        String jsonString = intent.getStringExtra(Method.resultKey);
                        Post post = convertToPost(jsonString);
                        ArrayList<Comment> comments = convertToComments(jsonString);
                        refreshListView(post, comments);
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
        // check scroll bottom key
        customLauncher_flag = getIntent().getBooleanExtra(Method.scrollBottomKey, false);
        getIntent().removeExtra(Method.scrollBottomKey);

        // request NetworkManager component to get data from server
        Intent intent = new Intent(this, NetworkManager.class);
        intent.putExtra(Method.methodKey, Method.getPostAndComments);
        intent.putExtra(Method.postIdKey, getIntent().getExtras().getInt(Method.postIdKey));
        startService(intent);
    }

    private Post convertToPost(String jsonString) {
        try {
            // convert JSON string to JSON array
            JSONObject jObj = new JSONObject(jsonString).getJSONObject("post");
            // construct post
            post = new Post(
                jObj.getInt("id"),
                jObj.getString("display_name"),
                jObj.getString("post_text"),
                jObj.getString("location"),
                jObj.getString("post_timestamp"),
                jObj.getInt("post_score"),
                jObj.getInt("reply_count"),
                jObj.getInt("user_rating"),
                jObj.getString("tempt_in_c"));
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON object " + e.toString());
        }
        return post;
    }

    private ArrayList<Comment> convertToComments(String jsonString) {
        try {
            // convert JSON string to JSON array
            JSONArray jArr = new JSONObject(jsonString).getJSONArray("comment");
            comments = new ArrayList<>();

            // construct element of posts
            for(int i=0; i<jArr.length(); i++) {
                JSONObject jObj = jArr.getJSONObject(i);
                comments.add( new Comment(
                    jObj.getInt("id"),
                    jObj.getInt("post_id"),
                    jObj.getString("comment_text"),
                    jObj.getString("comment_timestamp"),
                    jObj.getInt("comment_score"),
                    jObj.getInt("user_rating")) );
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON array " + e.toString());
        }
        return comments;
    }

    public void populateListView(Post post, ArrayList<Comment> comments) {
        SharedPreferences sharedPreferences = getSharedPreferences(Method.PREF_NAME, MODE_PRIVATE);
        int tempt_unit = sharedPreferences.getInt(Method.TEMPT_UNITS, Method.FAHRENHEIT);

        // create ArrayAdapter to manage data on the view
        adapter = new CommentViewerAdapter(this, post, comments, tempt_unit);

        // bind the adapter to ListView
        listView = (ListView) findViewById(R.id.listView);
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

        if(customLauncher_flag) {
            adapter.notifyDataSetChanged();
            listView.smoothScrollToPosition(adapter.getCount()-1);
        }
    }

    @Override
    public void onRefresh() {
        // reload all posts again
        requestPostAndComments();
    }

    public void createComment(View v) {
        String commenting_text = ((TextView)findViewById(R.id.commenting_text)).getText().toString();

        String postParameter = Url.postIdKey+Url.postAssigner+post.id+Url.postSeparator;
        postParameter += Url.commentTextKey+Url.postAssigner+commenting_text+Url.postSeparator;

        Intent intent = new Intent(this, NetworkManager.class);
        intent.putExtra(Method.methodKey, Method.createComment);
        intent.putExtra(Method.parameterKey,postParameter);
        startService(intent);
    }

    private void handleCreateComment(String jsonString) {
        // try to reload page silently
        try {
            // convert JSON string to JSON array
            JSONObject jObj = new JSONObject(jsonString);
            if(jObj.getBoolean(Url.statusKey)) {
                JSONObject commentObj = jObj.getJSONObject("comment");
                int postId = commentObj.getInt("post_id");

                Intent intent = new Intent(this, NetworkManager.class);
                intent.putExtra(Method.methodKey, Method.refreshCommentViewer);
                intent.putExtra(Method.postIdKey, postId);
                startService(intent);

                requestPostViewerRefresh();
            }
            else {
                Toast.makeText(this, jObj.getString(Url.errorMsgKey),Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON object " + e.toString());
        }
    }

    private void refreshListView(Post post, ArrayList<Comment> comments) {
        adapter.post = post;
        adapter.comments = comments;
        adapter.notifyDataSetChanged();
        listView.smoothScrollToPosition(adapter.getCount()-1);
        ((EditText)findViewById(R.id.commenting_text)).setText("");
    }

    public void handleRatePost(String jsonString) {
        try {
            JSONObject jObj = new JSONObject(jsonString);
            if(!jObj.getBoolean(Url.statusKey)) {
                String toastText = jObj.getString(Url.errorMsgKey);
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
                adapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON object " + e.toString());
        }
    }

    public void handleRateComment(String jsonString) {
        try {
            JSONObject jObj = new JSONObject(jsonString);
            if(!jObj.getBoolean(Url.statusKey)) {
                String toastText = jObj.getString(Url.errorMsgKey);
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
                adapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON object " + e.toString());
        }
    }

    protected void requestPostViewerRefresh() {
        Intent intent2 = new Intent(this, NetworkManager.class);
        intent2.putExtra(Method.methodKey, Method.refreshPostViewer);
        intent2.putExtra("location", "Los Angeles");
        startService(intent2);
    }

}
