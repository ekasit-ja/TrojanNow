package usc.cs578.trojannow.manager.post;

import android.view.View;
import android.widget.TextView;

import usc.cs578.com.trojannow.R;

/*
 * Created by Ekasit_Ja on 15-Apr-15.
 */
public class CommentHolder {
    TextView commentatorName;
    TextView commentText;
    TextView commentTimestamp;

    public CommentHolder(View v) {
        commentatorName = (TextView) v.findViewById(R.id.commentator_name);
        commentText = (TextView) v.findViewById(R.id.comment_text);
        commentTimestamp = (TextView) v.findViewById(R.id.comment_timestamp);
    }
}
