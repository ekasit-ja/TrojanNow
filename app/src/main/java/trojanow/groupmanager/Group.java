package trojanow.groupmanager;



import trojanow.usermanager.User;

import java.util.Collection;

/**
 * PURPOSE;
 * This class represents a group in the system. Users can use groups to publish posts to a specific
 * set of users. Posts published to a group can only be seen by users belonging to that group.
 *
 * ARCHITECTURAL MAPPING:
 * This class maps directly to the Group class in the class diagram.
 *
 * Created by Eirik Skogstad.
 */
public class Group {

    /**
     * Add a user to this group.
     *
     * @param user The user to be added.
     */
    public void addUser(User user) {

    }

    /**
     * Remove a user from this group.
     *
     * @param user The user to be removed.
     */
    public void removeUser(User user) {

    }

    /**
     * Returns a list of users belonging to the group.
     *
     * @return
     */
    public Collection getUsers() {
        return null;
    }
}
