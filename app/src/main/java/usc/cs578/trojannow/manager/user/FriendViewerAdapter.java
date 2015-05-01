package usc.cs578.trojannow.manager.user;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import usc.cs578.com.trojannow.R;
import usc.cs578.trojannow.manager.network.Method;
import usc.cs578.trojannow.manager.network.NetworkManager;

/*
 * Created by Ekasit_Ja on 18-Apr-15.
 */
public class FriendViewerAdapter extends BaseAdapter {

    private FriendViewer friendViewer;
    private ArrayList<Friend> friends;

    public FriendViewerAdapter(FriendViewer friendViewer, ArrayList<Friend> friends) {
        this.friendViewer = friendViewer;
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
        final FriendHolder holder;

        // build view holder style to recycling view object in order to improve performance
        if(row == null) {
			LayoutInflater inflater = (LayoutInflater) friendViewer.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.friend_in_search_list, parent, false);
            holder = new FriendHolder(row);
            row.setTag(holder);
        }else {
            holder = (FriendHolder) row.getTag();
        }

        final Friend friend = friends.get(position);

        holder.friend_name.setText(friend.name);

		final FriendHolder final_holder = holder;
        holder.btn_addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				friend.setIsFriend(true);
				RelativeLayout parent = (RelativeLayout) v.getParent();
				parent.findViewById(R.id.btn_addfriend).setVisibility(View.GONE);
				parent.findViewById(R.id.btn_unfriend).setVisibility(View.VISIBLE);
				addFriend(friend);
            }
        });

        holder.btn_unfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				friend.setIsFriend(false);
				RelativeLayout parent = (RelativeLayout) v.getParent();
				parent.findViewById(R.id.btn_addfriend).setVisibility(View.VISIBLE);
				parent.findViewById(R.id.btn_unfriend).setVisibility(View.GONE);
				removeFriend(friend);
            }
        });

        if (friend.isFriend) {
            holder.btn_unfriend.setVisibility(View.VISIBLE);
            holder.btn_addfriend.setVisibility(View.GONE);
        } else {
            holder.btn_unfriend.setVisibility(View.GONE);
            holder.btn_addfriend.setVisibility(View.VISIBLE);
        }

        row.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Profile.class);
                intent.putExtra(Method.userIdKey, friend.getId());
                context.startActivity(intent);
            }
        });

        return row;
    }



    private void addFriend(Friend friend) {
        Intent intent = new Intent(this.friendViewer, NetworkManager.class);
        intent.putExtra(Method.methodKey, Method.updateFriend);
        intent.putExtra(Method.parameterKey, "add");
        intent.putExtra(Method.userIdKey, friend.id);
        this.friendViewer.startService(intent);

        friendViewer.updateListView();
    }

    private void removeFriend(Friend friend) {
        Intent intent = new Intent(this.friendViewer, NetworkManager.class);
        intent.putExtra(Method.methodKey, Method.updateFriend);
        intent.putExtra(Method.parameterKey, "remove");
        intent.putExtra(Method.userIdKey, friend.id);
        this.friendViewer.startService(intent);

        friendViewer.updateListView();
    }
}
