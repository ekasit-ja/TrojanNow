package usc.cs578.trojannow.manager.post;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import usc.cs578.com.trojannow.R;
import usc.cs578.trojannow.manager.network.Method;
import usc.cs578.trojannow.manager.network.NetworkManager;
import usc.cs578.trojannow.manager.network.Url;

/*
 * Created by Ekasit_Ja on 15-Apr-15.
 */
public class CommentViewerAdapter extends BaseAdapter {

    private static final String TAG = CommentViewerAdapter.class.getSimpleName();
    private static final int TYPE_POST = 0;
    private static final int TYPE_COMMENT = 1;
    private static final String REPLY = "reply";
    private static final String REPLIES = "replies";

    protected CommentViewer commentViewer;
    private int TEMPT_UNIT;


    public CommentViewerAdapter(CommentViewer commentViewer, int TEMPT_UNIT) {
        this.commentViewer = commentViewer;
        this.TEMPT_UNIT = TEMPT_UNIT;
    }

    @Override
    public int getCount() {
        return commentViewer.comments.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if(position == 0) {
            return commentViewer.post;
        }
        else {
            return commentViewer.comments.get(position-1);
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
                    LayoutInflater inflater = (LayoutInflater) commentViewer.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row = inflater.inflate(R.layout.post_in_comment_viewer, parent, false);
                    postHolder = new PostHolder(row);
                    row.setTag(postHolder);

                    // set tag for rating button
                    postHolder.plus_button.setTag(postHolder);
                    postHolder.minus_button.setTag(postHolder);
                } else {
                    postHolder = (PostHolder) row.getTag();
                }

                // set values of views on a single row of each post in the dashboard
                if(commentViewer.post.posterName.length() < 1) {
                    // if it is anonymous, remove name and extra info elements
                    postHolder.poster_name.setVisibility(View.GONE);
                }
                else {
                    // set visibility to show again in case recycling
                    postHolder.poster_name.setVisibility(View.VISIBLE);
                    postHolder.poster_name.setText(commentViewer.post.posterName);
                }

                if(commentViewer.post.location.length() < 1) {
                    postHolder.location.setVisibility(View.GONE);
                }
                else {
                    postHolder.location.setVisibility(View.VISIBLE);
                    postHolder.location.setText(commentViewer.post.location);
                }

                // manage tempt

                String tempt = commentViewer.post.tempt_in_c;
                if(tempt.length() > 0) {
                    postHolder.tempt_label.setVisibility(View.VISIBLE);
                    if (TEMPT_UNIT == Method.CELSIUS) {
                        postHolder.tempt_label.setText(tempt + commentViewer.getString(R.string.celsius_suffix));
                    } else {
                        int t = Integer.parseInt(tempt);
                        t = (t * 9 / 5) + 32;
                        postHolder.tempt_label.setText(t + commentViewer.getString(R.string.fahrenheit_suffix));
                    }
                }
                else {
                    postHolder.tempt_label.setVisibility(View.GONE);
                }

                postHolder.post_text.setText(commentViewer.post.postText);
                // calculate elapsed time to show pretty word instead of full time
                String postTimeText = commentViewer.post.postTimestamp;
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

                int ratingScore = commentViewer.post.postScore;
                postHolder.rating_score.setText(String.valueOf(ratingScore));

                int replyCount = commentViewer.post.replyCount;
                postHolder.reply_count.setText(String.valueOf(replyCount));

                if(replyCount == 0) {
                    postHolder.reply_count.setVisibility(View.GONE);
                    postHolder.reply_label.setVisibility(View.GONE);
                }
                else if(replyCount == 1) {
                    postHolder.reply_count.setVisibility(View.VISIBLE);
                    postHolder.reply_label.setVisibility(View.VISIBLE);
                    postHolder.reply_label.setText(REPLY);
                }
                else {
                    postHolder.reply_count.setVisibility(View.VISIBLE);
                    postHolder.reply_label.setVisibility(View.VISIBLE);
                    postHolder.reply_label.setText(REPLIES);
                }

                // manage rating button color
                int userRating = commentViewer.post.userRating;
                if(userRating == 1) {
                    // plus button selected
                    postHolder.plus_button.setImageResource(R.mipmap.ic_plus_selected);
                    postHolder.minus_button.setImageResource(R.mipmap.ic_minus);
                }
                else if (userRating == -1) {
                    // minus button selected
                    postHolder.plus_button.setImageResource(R.mipmap.ic_plus);
                    postHolder.minus_button.setImageResource(R.mipmap.ic_minus_selected);
                }
                else {
                    postHolder.plus_button.setImageResource(R.mipmap.ic_plus);
                    postHolder.minus_button.setImageResource(R.mipmap.ic_minus);
                }

                final PostHolder final_postHolder = postHolder;
                postHolder.plus_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int currentScore = commentViewer.post.postScore;
                        int oldRating = commentViewer.post.userRating;
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
                        commentViewer.post.userRating = newRating;
                        commentViewer.post.postScore = currentScore+scoreChange;
                        doRate(TYPE_POST, commentViewer.post.id, newRating, currentScore + scoreChange,
                                final_postHolder.plus_button, final_postHolder.minus_button, final_postHolder.rating_score);
                    }
                });
                postHolder.minus_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int currentScore = commentViewer.post.postScore;
                        int oldRating = commentViewer.post.userRating;
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
                        commentViewer.post.userRating = newRating;
                        commentViewer.post.postScore = currentScore+scoreChange;
                        doRate(TYPE_POST, commentViewer.post.id, newRating, currentScore + scoreChange,
                                final_postHolder.plus_button, final_postHolder.minus_button, final_postHolder.rating_score);
                    }
                });

                break;
            }
            case TYPE_COMMENT: {
                if (row == null) {
                    LayoutInflater inflater = (LayoutInflater) commentViewer.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row = inflater.inflate(R.layout.comment_in_comment_viewer, parent, false);
                    commentHolder = new CommentHolder(row);
                    row.setTag(commentHolder);

                    // set tag for rating button
                    commentHolder.plus_button.setTag(commentHolder);
                    commentHolder.minus_button.setTag(commentHolder);
                } else {
                    commentHolder = (CommentHolder) row.getTag();
                }

                // set values of views on a single row of each post in the dashboard
                int index = position - 1;
                commentHolder.commentText.setText(commentViewer.comments.get(index).commentText);
                // calculate elapsed time to show pretty word instead of full time
                String postTimeText = commentViewer.comments.get(index).commentTimestamp;
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
                commentHolder.commentScore.setText(String.valueOf(commentViewer.comments.get(index).commentScore));

                int commentRating = commentViewer.comments.get(index).commentRating;
                if(commentRating == 1) {
                    commentHolder.plus_button.setImageResource(R.mipmap.ic_plus_selected);
                    commentHolder.minus_button.setImageResource(R.mipmap.ic_minus);
                }
                else if(commentRating == -1) {
                    commentHolder.plus_button.setImageResource(R.mipmap.ic_plus);
                    commentHolder.minus_button.setImageResource(R.mipmap.ic_minus_selected);
                }
                else {
                    commentHolder.plus_button.setImageResource(R.mipmap.ic_plus);
                    commentHolder.minus_button.setImageResource(R.mipmap.ic_minus);
                }

                final CommentHolder final_commentHolder = commentHolder;
                final int final_index = index;
                commentHolder.plus_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int currentScore = commentViewer.comments.get(final_index).commentScore;
                        int oldRating = commentViewer.comments.get(final_index).commentRating;
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
                        commentViewer.comments.get(final_index).commentRating = newRating;
                        commentViewer.comments.get(final_index).commentScore = currentScore+scoreChange;
                        doRate(TYPE_COMMENT, commentViewer.comments.get(final_index).id, newRating, currentScore + scoreChange,
                                final_commentHolder.plus_button, final_commentHolder.minus_button, final_commentHolder.commentScore);
                    }
                });
                commentHolder.minus_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int currentScore = commentViewer.comments.get(final_index).commentScore;
                        int oldRating = commentViewer.comments.get(final_index).commentRating;
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
                        commentViewer.comments.get(final_index).commentRating = newRating;
                        commentViewer.comments.get(final_index).commentScore = currentScore+scoreChange;
                        doRate(TYPE_COMMENT, commentViewer.comments.get(final_index).id, newRating, currentScore + scoreChange,
                                final_commentHolder.plus_button, final_commentHolder.minus_button, final_commentHolder.commentScore);
                    }
                });

                break;
            }
        }

        return row;
    }

    public void doRate(int type, int id, int newRating, int newScore,
                           ImageButton plus, ImageButton minus, TextView score) {

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

        if(type == TYPE_POST) {
            score.setText(newScore + "");
            String parameter = Url.postIdKey + Url.postAssigner + id + Url.postSeparator;
            parameter += Url.ratingScoreKey + Url.postAssigner + newRating + Url.postSeparator;
            parameter += Url.newScoreKey + Url.postAssigner + newScore + Url.postSeparator;

            // request NetworkManager component to login
            Intent intent = new Intent(commentViewer, NetworkManager.class);
            intent.putExtra(Method.methodKey, Method.ratePostFromComment);
            intent.putExtra(Method.parameterKey, parameter);
            commentViewer.startService(intent);
        }
        else {
            score.setText(newScore + "");
            String parameter = Url.commentIdKey + Url.postAssigner + id + Url.postSeparator;
            parameter += Url.ratingScoreKey + Url.postAssigner + newRating + Url.postSeparator;
            parameter += Url.newScoreKey + Url.postAssigner + newScore + Url.postSeparator;

            // request NetworkManager component to login
            Intent intent = new Intent(commentViewer, NetworkManager.class);
            intent.putExtra(Method.methodKey, Method.rateComment);
            intent.putExtra(Method.parameterKey, parameter);
            commentViewer.startService(intent);
        }
    }

}
