package usc.cs578.trojannow.manager.post;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import usc.cs578.com.trojannow.R;

/*
 * Created by Ekasit_Ja on 14-Apr-15.
 */
public class PostViewerAdapter extends BaseAdapter {
    private static final String TAG = "PostViewerAdapter";
    private static final int READ_MORE_OFFSET = 11;
    private static final int PAINT_OFFSET = 9;
    private static final String READ_MORE_SUFFIX = "… read more";
    private int PAINT_COLOR;

    private Context context;
    private Post[] posts;

    public PostViewerAdapter(Context context, Post[] posts) {
        this.context = context;
        this.posts = posts;
        PAINT_COLOR = context.getResources().getColor(R.color.grey);
    }

    @Override
    public int getCount() {
        return posts.length;
    }

    @Override
    public Object getItem(int position) {
        return posts[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PostHolder holder;

        // build view holder style to recycling view object in order to improve performance
        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.post_in_post_viewer, parent, false);
            holder = new PostHolder(row);
            row.setTag(holder);
        }
        else {
            holder = (PostHolder) row.getTag();
        }

        // set values of views on a single row of each post in the dashboard
        if(posts[position].posterName.length() < 1) {
            // if it is anonymous, remove name and extra info elements
            holder.poster_name.setVisibility(View.GONE);
            holder.location.setVisibility(View.GONE);
        }
        else {
            // set visibility to show again in case recycling
            holder.poster_name.setVisibility(View.VISIBLE);
            holder.location.setVisibility(View.VISIBLE);
            holder.poster_name.setText(posts[position].posterName);
            holder.location.setText(posts[position].location);
        }

        holder.post_text.setText(posts[position].postText);

        // add thread to run after text view is set in order to check if post_text
        // is ellipsized or not.  If so, handle the case.
        final TextView tv = holder.post_text;
        tv.post(new Runnable() {
            @Override
            public void run() {
                Layout l = tv.getLayout();
                if( l != null) {
                    int lines = l.getLineCount();
                    if(lines > 0) {
                        if(l.getEllipsisCount(lines - 1) > 0) {
                            // find the length of last row, if it is less than offset, simply
                            // add suffix to the row, if not, substring beforehand
                            int lineEndIndex = tv.getLayout().getLineEnd(lines - 1);
                            int lineSubEndIndex = tv.getLayout().getLineEnd(lines - 2);
                            String newText;
                            if (lineEndIndex - lineSubEndIndex < READ_MORE_OFFSET) {
                                // -1 to remove line feed character
                                newText = tv.getText().subSequence(0, lineEndIndex - 1).toString();
                            } else {
                                newText = tv.getText().subSequence(0, lineEndIndex - READ_MORE_OFFSET).toString();
                            }

                            // make new text spannable to paint suffix text with grey color
                            SpannableStringBuilder sb = new SpannableStringBuilder(newText + READ_MORE_SUFFIX);
                            ForegroundColorSpan fcs = new ForegroundColorSpan(PAINT_COLOR);
                            sb.setSpan(fcs, sb.length()-PAINT_OFFSET, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                            tv.setText(sb);
                        }
                    }
                }
            }
        });

        // calculate elapsed time to show pretty word instead of full time
        String postTimeText = posts[position].postTimestamp;
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
        holder.post_timestamp.setText(elapsedTimeText);

        // add listener to the row
        final int finalPosition = position;
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send intent to initiate activity post editor
                Intent intent = new Intent(context, CommentViewer.class);
                intent.putExtra("postId", PostViewerAdapter.this.posts[finalPosition].id);
                context.startActivity(intent);
            }
        });

        return row;
    }

}
