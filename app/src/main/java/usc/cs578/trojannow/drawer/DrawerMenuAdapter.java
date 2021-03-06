package usc.cs578.trojannow.drawer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import usc.cs578.com.trojannow.R;

import usc.cs578.trojannow.manager.chat.Chat;

import usc.cs578.trojannow.manager.network.Method;
import usc.cs578.trojannow.manager.network.NetworkManager;
import usc.cs578.trojannow.manager.network.Url;
import usc.cs578.trojannow.manager.post.PostViewer;
import usc.cs578.trojannow.manager.user.FriendViewer;
import usc.cs578.trojannow.manager.user.Login;
import usc.cs578.trojannow.manager.user.Profile;
import usc.cs578.trojannow.manager.user.Settings;

/*
 * Created by Ekasit_Ja on 17-Apr-15.
 */
public class DrawerMenuAdapter extends BaseAdapter {
    private static final String TAG = "DrawerMenuAdapter";
    private static final int ID_LOGIN = 0;
    private static final int ID_SETTINGS = 1;
    private static final int ID_FRIENDS = 2;
    private static final int ID_CHAT = 3;
    private static final int ID_MYPROFILE = 4;
    private static final int ID_LOGOUT = 5;

    private Context context;
    private DrawerLayout drawerLayout;
    private DrawerMenuItem[] drawerMenuItems;
    private SharedPreferences sharedPreferences;

    public DrawerMenuAdapter(Context context, DrawerLayout drawerLayout) {
        this.context = context;
        this.drawerLayout = drawerLayout;

        // check login
        sharedPreferences = context.getSharedPreferences(Method.PREF_NAME, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Url.sessionIdKey, "");

        // populate menu items
        if(sessionId.length() > 0) {
            // already log in
            drawerMenuItems = new DrawerMenuItem[5];
            drawerMenuItems[0] = new DrawerMenuItem(ID_FRIENDS, "Friends");
            drawerMenuItems[1] = new DrawerMenuItem(ID_SETTINGS, "Settings");
            drawerMenuItems[2] = new DrawerMenuItem(ID_CHAT,"Chat");
            drawerMenuItems[3] = new DrawerMenuItem(ID_MYPROFILE,"My Profile");
            drawerMenuItems[4] = new DrawerMenuItem(ID_LOGOUT, "Sign out");

        }
        else {
            // not log in
            drawerMenuItems = new DrawerMenuItem[2];
            drawerMenuItems[0] = new DrawerMenuItem(ID_LOGIN, "Sign in");
            drawerMenuItems[1] = new DrawerMenuItem(ID_SETTINGS, "Settings");
        }
    }

    @Override
    public int getCount() {
        return drawerMenuItems.length;
    }

    @Override
    public Object getItem(int position) {
        return drawerMenuItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        DrawerMenuItemHolder holder;

        // build view holder style to recycling view object in order to improve performance
        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.menu_item_in_drawer, parent, false);
            holder = new DrawerMenuItemHolder(row);
            row.setTag(holder);
        }
        else {
            holder = (DrawerMenuItemHolder) row.getTag();
        }

        holder.menuItem.setText(drawerMenuItems[position].content);

        // add listener to the row
        final int finalPosition = position;
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();

                // // send intent to initiate activity post editor after closing drawer 300ms
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent;
                        switch (drawerMenuItems[finalPosition].id) {
                            case ID_LOGIN: {
                                intent = new Intent(context, Login.class);
                                break;
                            }
                            case ID_FRIENDS: {
                                intent = new Intent(context, FriendViewer.class);
                                break;
                            }
                            case ID_SETTINGS: {
                                intent = new Intent(context, Settings.class);
                                break;
                            }
                            case ID_CHAT: {
                                intent = new Intent(context, Chat.class);
                                break;
                            }
                            case ID_MYPROFILE: {
                                intent = new Intent(context, Profile.class);
                                intent.putExtra(Method.userIdKey, "active");
                                break;
                            }
                            case ID_LOGOUT: {
                                doLogout();
                                return;
                            }
                            default: {
                                Log.w(TAG, "Drawer menu falls case default");
                                return;
                            }
                        }
                        context.startActivity(intent);
                    }
                }, 300);
            }
        });

        return row;
    }

    private void doLogout() {
        // notify post viewer to refresh page
        Intent intent = new Intent(context, NetworkManager.class);
        intent.putExtra(Method.methodKey, Method.logout);
        context.startService(intent);
    }
}
