package usc.cs578.trojannow.manager.post;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import usc.cs578.com.trojannow.R;

/*
 * Created by Ekasit_Ja on 14-Apr-15.
 */
public class PostHolder {
    TextView poster_name;
    TextView location;
    TextView post_text;
    TextView post_timestamp;
    TextView reply_count;
    TextView reply_label;
    TextView rating_score;
    TextView tempt_label;
    ImageButton plus_button;
    ImageButton minus_button;

    public PostHolder(View v) {
        poster_name = (TextView) v.findViewById(R.id.poster_name);
        location = (TextView) v.findViewById(R.id.location);
        post_text = (TextView) v.findViewById(R.id.post_text);
        post_timestamp = (TextView) v.findViewById(R.id.post_timestamp);
        reply_count = (TextView) v.findViewById(R.id.reply_count);
        reply_label = (TextView) v.findViewById(R.id.reply_label);
        rating_score = (TextView) v.findViewById(R.id.rating_score);
        tempt_label = (TextView) v.findViewById(R.id.tempt_label);
        plus_button = (ImageButton) v.findViewById(R.id.plus_button);
        minus_button = (ImageButton) v.findViewById(R.id.minus_button);
    }

}
