package usc.cs578.trojannow.manager.user;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import usc.cs578.com.trojannow.R;

/*
 * Created by Ekasit_Ja on 18-Apr-15.
 */
public class FriendHolder {
    protected ImageView imageView;
    protected TextView friend_name;

    public FriendHolder(View v) {
        friend_name = (TextView) v.findViewById(R.id.friend_name);
        imageView = (ImageView) v.findViewById(R.id.imageView);
    }
}
