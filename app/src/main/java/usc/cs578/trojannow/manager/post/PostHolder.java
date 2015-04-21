package usc.cs578.trojannow.manager.post;

import android.view.View;
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

    public PostHolder(View v) {
        poster_name = (TextView) v.findViewById(R.id.poster_name);
        location = (TextView) v.findViewById(R.id.location);
        post_text = (TextView) v.findViewById(R.id.post_text);
        post_timestamp = (TextView) v.findViewById(R.id.post_timestamp);
    }

}
