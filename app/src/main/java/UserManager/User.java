package UserManager;

import GroupManager.Group;

import java.util.Collection;

/**
 * Contains all information about a specific user in the system.
 *
 * Created by echo on 3/30/15.
 */
public class User {

    /**
     * Get the name of this user.
     *
     * @return The name of the user.
     */
    public String getName() {
        return null;
    }

    /**
     * Add a user to this user's list of friends.
     *
     * @param friend The user to be added as a friend.
     */
    public void addFriend(User friend) {

    }

    /**
     * Block another user. This will prevent the other user from
     * sending friend requests or seeing the user's profile.
     *
     * @param user The user to be blocked.
     */
    public void blockUser(User user) {

    }

    /**
     * Unblock a user. Reverses the action from blockUser.
     *
     * @param user The user to be unblocked.
     */
    public void unblockUser(User user) {

    }

    /**
     * Returns a list of this user's friends.
     *
     * @return
     */
    public Collection getFriends() {
        return null;
    }

    /**
     * Become member in a group.
     *
     * @param group The group which the user wants to join.
     */
    public void addGroup(Group group) {

    }

    /**
     * Returns a list of all the groups the user has joined.
     *
     * @return
     */
    public Collection getGroups() {
        return null;
    }
}
