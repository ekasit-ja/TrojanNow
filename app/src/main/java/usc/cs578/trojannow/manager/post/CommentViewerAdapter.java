package usc.cs578.trojannow.manager.post;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import usc.cs578.com.trojannow.R;

/*
 * Created by Ekasit_Ja on 15-Apr-15.
 */
public class CommentViewerAdapter extends BaseAdapter {

    private static final String TAG = CommentViewerAdapter.class.getSimpleName();
    private static final int TYPE_POST = 0;
    private static final int TYPE_COMMENT = 1;

    private Context context;
    private Post post;
    private Comment[] comments;

    public CommentViewerAdapter(Context context, Post post, Comment[] comments) {
        this.context = context;
        this.post = post;
        this.comments = comments;
    }

    @Override
    public int getCount() {
        return comments.length + 1;
    }

    @Override
    public Object getItem(int position) {
        if(position == 0) {
            return post;
        }
        else {
            return comments[position-1];
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // override 2 below methods to use with "getView"
    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return TYPE_POST;
        }
        else {
            return TYPE_COMMENT;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = this.getItemViewType(position);
        View row = convertView;
        PostHolder postHolder;
        CommentHolder commentHolder;

        switch(viewType) {
            case TYPE_POST: {
                if (row == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row = inflater.inflate(R.layout.post_in_comment_viewer, parent, false);
                    postHolder = new PostHolder(row);
                    row.setTag(postHolder);
                } else {
                    postHolder = (PostHolder) row.getTag();
                }

                // set values of views on a single row of each post in the dashboard
                if (post.posterName.length() < 1) {
                    // if it is anonymous, remove name and extra info elements
                    postHolder.poster_name.setVisibility(View.GONE);
                    postHolder.location.setVisibility(View.GONE);
                } else {
                    // set visible again in case of recycling
                    postHolder.poster_name.setVisibility(View.VISIBLE);
                    postHolder.location.setVisibility(View.VISIBLE);
                    postHolder.poster_name.setText(post.posterName);
                    postHolder.location.setText(post.location);
                }

                postHolder.post_text.setText(post.postText);
                // calculate elapsed time to show pretty word instead of full time
                String postTimeText = post.postTimestamp;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                String elapsedTimeText = "";
                try {
                    elapsedTimeText = DateUtils.getRelativeTimeSpanString(
                            dateFormat.parse(postTimeText).getTime(),
                            System.currentTimeMillis(),
                            DateUtils.SECOND_IN_MILLIS,
                            0).toString();
                } catch (ParseException e) {
                    Log.e(TAG, "Error converting string of post time to date " + e.toString());
                }
                postHolder.post_timestamp.setText(elapsedTimeText);
                break;
            }
            case TYPE_COMMENT: {
                if (row == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row = inflater.inflate(R.layout.comment_in_comment_viewer, parent, false);
                    commentHolder = new CommentHolder(row);
                    row.setTag(commentHolder);
                } else {
                    commentHolder = (CommentHolder) row.getTag();
                }

                // set values of views on a single row of each post in the dashboard
                int index = position - 1;
                if (comments[index].commentatorName.length() < 1) {
                    // if it is anonymous, remove name and extra info elements
                    commentHolder.commentatorName.setVisibility(View.GONE);
                } else {
                    // set visible again in case of recycling
                    commentHolder.commentatorName.setVisibility(View.VISIBLE);
                    commentHolder.commentatorName.setText(comments[index].commentatorName);
                }

                commentHolder.commentText.setText(comments[index].commentText);
                // calculate elapsed time to show pretty word instead of full time
                String postTimeText = comments[index].commentTimestamp;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                String elapsedTimeText = "";
                try {
                    elapsedTimeText = DateUtils.getRelativeTimeSpanString(
                            dateFormat.parse(postTimeText).getTime(),
                            System.currentTimeMillis(),
                            DateUtils.SECOND_IN_MILLIS,
                            0).toString();
                } catch (ParseException e) {
                    Log.e(TAG,"Error converting string of post time to date "+e.toString());
                }
                commentHolder.commentTimestamp.setText(elapsedTimeText);
                break;
            }
        }

        return row;
    }

}
