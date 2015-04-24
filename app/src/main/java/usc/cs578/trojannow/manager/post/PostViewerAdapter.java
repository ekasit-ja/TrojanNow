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
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import usc.cs578.com.trojannow.R;
import usc.cs578.trojannow.manager.network.Method;
import usc.cs578.trojannow.manager.network.NetworkManager;
import usc.cs578.trojannow.manager.network.Url;

/*
 * Created by Ekasit_Ja on 14-Apr-15.
 */
public class PostViewerAdapter extends BaseAdapter {
    private static final String TAG = PostViewerAdapter.class.getSimpleName();
    private static final int READ_MORE_OFFSET = 11;
    private static final int PAINT_OFFSET = 9;
    private static final String READ_MORE_SUFFIX = "… read more";
    private static final String REPLY = "reply";
    private static final String REPLIES = "replies";
    private int PAINT_COLOR;

    private Context context;
    private ArrayList<Post> posts;
    private int TEMPT_UNIT;

    public PostViewerAdapter(Context context, ArrayList<Post> posts, int tempt_unit) {
        this.context = context;
        this.posts = posts;
        this.TEMPT_UNIT = tempt_unit;
        PAINT_COLOR = context.getResources().getColor(R.color.grey);
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
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

            // set tag for rating button
            holder.plus_button.setTag(holder);
            holder.minus_button.setTag(holder);
        }
        else {
            holder = (PostHolder) row.getTag();
        }

        // set values of views on a single row of each post in the dashboard
        if(posts.get(position).posterName.length() < 1) {
            // if it is anonymous, remove name and extra info elements
            holder.poster_name.setVisibility(View.GONE);
        }
        else {
            // set visibility to show again in case recycling
            holder.poster_name.setVisibility(View.VISIBLE);
            holder.poster_name.setText(posts.get(position).posterName);
        }

        if(posts.get(position).location.length() < 1) {
            holder.location.setVisibility(View.GONE);
        }
        else {
            holder.location.setVisibility(View.VISIBLE);
            holder.location.setText(posts.get(position).location);
        }

        // manage tempt

        String tempt = posts.get(position).tempt_in_c;
        if(tempt.length() > 0) {
            holder.tempt_label.setVisibility(View.VISIBLE);
            if (TEMPT_UNIT == Method.CELSIUS) {
                holder.tempt_label.setText(tempt + context.getString(R.string.celsius_suffix));
            } else {
                int t = Integer.parseInt(tempt);
                t = (t * 9 / 5) + 32;
                holder.tempt_label.setText(t + context.getString(R.string.fahrenheit_suffix));
            }
        }
        else {
            holder.tempt_label.setVisibility(View.GONE);
        }

        holder.post_text.setText(posts.get(position).postText);

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
        String postTimeText = posts.get(position).postTimestamp;
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

        int ratingScore = posts.get(position).postScore;
        holder.rating_score.setText(String.valueOf(ratingScore));

        int replyCount = posts.get(position).replyCount;
        holder.reply_count.setText(String.valueOf(replyCount));

        if(replyCount == 0) {
            holder.reply_count.setVisibility(View.GONE);
            holder.reply_label.setVisibility(View.GONE);
        }
        else if(replyCount == 1) {
            holder.reply_count.setVisibility(View.VISIBLE);
            holder.reply_label.setVisibility(View.VISIBLE);
            holder.reply_label.setText(REPLY);
        }
        else {
            holder.reply_count.setVisibility(View.VISIBLE);
            holder.reply_label.setVisibility(View.VISIBLE);
            holder.reply_label.setText(REPLIES);
        }

        // manage rating button color
        int userRating = posts.get(position).userRating;
        if(userRating == 1) {
            // plus button selected
            holder.plus_button.setImageResource(R.mipmap.ic_plus_selected);
            holder.minus_button.setImageResource(R.mipmap.ic_minus);
        }
        else if (userRating == -1) {
            // minus button selected
            holder.plus_button.setImageResource(R.mipmap.ic_plus);
            holder.minus_button.setImageResource(R.mipmap.ic_minus_selected);
        }
        else {
            holder.plus_button.setImageResource(R.mipmap.ic_plus);
            holder.minus_button.setImageResource(R.mipmap.ic_minus);
        }

        final PostHolder final_holder = holder;
        final int final_position = position;
        holder.plus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentScore = posts.get(final_position).postScore;
                int oldRating = posts.get(final_position).userRating;
                int newRating;
                int scoreChange;
                if(oldRating == 1) {
                    newRating = 0;
                    scoreChange = -1;
                }
                else if(oldRating == -1) {
                    newRating = 1;
                    scoreChange = 2;
                }
                else {
                    newRating = 1;
                    scoreChange = 1;
                }
                posts.get(final_position).userRating = newRating;
                posts.get(final_position).postScore = currentScore+scoreChange;
                doRate(posts.get(final_position).id, newRating, currentScore+scoreChange,
                        final_holder.plus_button, final_holder.minus_button, final_holder.rating_score);
            }
        });
        holder.minus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentScore = posts.get(final_position).postScore;
                int oldRating = posts.get(final_position).userRating;
                int newRating;
                int scoreChange;
                if(oldRating == -1) {
                    newRating = 0;
                    scoreChange = 1;
                }
                else if(oldRating == 1) {
                    newRating = -1;
                    scoreChange = -2;
                }
                else {
                    newRating = -1;
                    scoreChange = -1;
                }
                posts.get(final_position).userRating = newRating;
                posts.get(final_position).postScore = currentScore+scoreChange;
                doRate(posts.get(final_position).id, newRating, currentScore+scoreChange,
                        final_holder.plus_button, final_holder.minus_button, final_holder.rating_score);
            }
        });

        // add listener to the row
        final int finalPosition = position;
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send intent to initiate activity post editor
                Intent intent = new Intent(context, CommentViewer.class);
                intent.putExtra(Method.postIdKey, PostViewerAdapter.this.posts.get(finalPosition).id);
                context.startActivity(intent);
            }
        });

        return row;
    }

    public void doRate(int postId, int newRating, int newScore, ImageButton plus, ImageButton minus, TextView score) {
        if(newRating == 1) {
            plus.setImageResource(R.mipmap.ic_plus_selected);
            minus.setImageResource(R.mipmap.ic_minus);
        }
        else if(newRating == -1) {
            plus.setImageResource(R.mipmap.ic_plus);
            minus.setImageResource(R.mipmap.ic_minus_selected);
        }
        else {
            plus.setImageResource(R.mipmap.ic_plus);
            minus.setImageResource(R.mipmap.ic_minus);

        }

        score.setText(newScore+"");
        String parameter = Url.postIdKey+Url.postAssigner+postId+Url.postSeparator;
        parameter += Url.ratingScoreKey+Url.postAssigner+newRating+Url.postSeparator;
        parameter += Url.newScoreKey+Url.postAssigner+newScore+Url.postSeparator;

        // request NetworkManager component to login
        Intent intent = new Intent(context, NetworkManager.class);
        intent.putExtra(Method.methodKey, Method.ratePost);
        intent.putExtra(Method.parameterKey, parameter);
        context.startService(intent);
    }
}
