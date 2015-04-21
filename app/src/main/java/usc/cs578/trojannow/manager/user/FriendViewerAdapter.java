package usc.cs578.trojannow.manager.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import usc.cs578.com.trojannow.R;

/*
 * Created by Ekasit_Ja on 18-Apr-15.
 */
public class FriendViewerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Friend> friends;

    public FriendViewerAdapter(Context context, ArrayList<Friend> friends) {
        this.context = context;
        this.friends = friends;
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Object getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        FriendHolder holder;

        // build view holder style to recycling view object in order to improve performance
        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.friend_in_search_list, parent, false);
            holder = new FriendHolder(row);
            row.setTag(holder);
        }
        else {
            holder = (FriendHolder) row.getTag();
        }

        holder.friend_name.setText(friends.get(position).name);

        return row;
    }
}
