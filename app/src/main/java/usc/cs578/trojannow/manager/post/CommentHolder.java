package usc.cs578.trojannow.manager.post;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import usc.cs578.com.trojannow.R;

/*
 * Created by Ekasit_Ja on 15-Apr-15.
 */
public class CommentHolder {
    TextView commentatorName;
    TextView commentText;
    TextView commentTimestamp;
    TextView commentScore;
    ImageButton plus_button;
    ImageButton minus_button;

    public CommentHolder(View v) {
        commentatorName = (TextView) v.findViewById(R.id.commentator_name);
        commentText = (TextView) v.findViewById(R.id.comment_text);
        commentTimestamp = (TextView) v.findViewById(R.id.comment_timestamp);
        commentScore = (TextView) v.findViewById(R.id.rating_score);
        plus_button = (ImageButton) v.findViewById(R.id.plus_button);
        minus_button = (ImageButton) v.findViewById(R.id.minus_button);
    }
}
