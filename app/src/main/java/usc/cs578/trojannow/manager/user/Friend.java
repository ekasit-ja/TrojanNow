package usc.cs578.trojannow.manager.user;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/*
 * Created by Ekasit_Ja on 18-Apr-15.
 */
public class Friend {
    protected int id;
    protected String name;
    protected Boolean isFriend;

    public Friend(int id, String name, Boolean isFriend) {
        this.id = id;
        this.name = name;
        this.isFriend = isFriend;
    }

    public static void getFriendsFromJSON(String jsonString, ArrayList<Friend> friends) {
        try {
            JSONObject jObj = new JSONObject(jsonString);
            JSONArray friendsArray = jObj.getJSONArray("users");

            for(int i=0; i<friendsArray.length(); i++) {
                JSONObject userObj = friendsArray.getJSONObject(i);

                Boolean isFriend = userObj.has("friend") ? Boolean.parseBoolean(userObj.getString("friend")) : true;
                Friend friend = new Friend(userObj.getInt("id"), userObj.getString("display_name"), isFriend);
                friends.add(friend);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
