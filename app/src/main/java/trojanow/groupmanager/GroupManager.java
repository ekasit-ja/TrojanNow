package trojanow.groupmanager;

import android.app.Activity;
import android.content.Intent;
import trojanow.interfaces.Invokable;

/**
 * PURPOSE;
 * Allows users to create, delete, join and leave groups.
 *
 * OPERATION:
 * This class will present a screen which lists current groups in the system, and
 * allow the user to perform operations on these groups or create new groups.
 *
 * Group messages will be communicated to other components via the LocalBroadcastManager,
 * and this class will listen for messages from other components with a BroadcastReceiver.
 * Since changes in groups must be communicated to the server, the principal route for
 * this kind of messages will be between GroupManager and NetworkManager.
 *
 * ARCHITECTURAL MAPPING:
 * This class is part of the Group Manager component in the architectural diagram, and maps
 * directly to the GroupManager class in the class diagram.
 *
 * Created by Eirik Skogstad.
 */
public class GroupManager extends Activity implements Invokable {

    /**
     * Called when the class receives an intent through a BroadcastReceiver
     */
    @Override
    public void onReceiveIntent(Intent intent) {

    }

    /**
     * Create a new group.
     */
    public void createGroup() {

    }

    /**
     * Delete a group.
     */
    public void deleteGroup() {

    }

    /**
     * Add user to a group.
     */
    public void joinGroup() {

    }

    /**
     * Remove user from a group.
     */
    public void leaveGroup() {

    }
}
