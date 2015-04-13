package UserManager;

import Interfaces.Invokable;
import android.app.Activity;
import android.content.Intent;

/**
 * PURPOSE:
 * This class will show a list of pending friend requests for the user,
 * and also the current friends of the user. The user can accept or decline
 * friend requests or block or unblock other users.
 *
 * OPERATION:
 * This class displays a screen which lists incoming friend requests and existing friends.
 *
 * ARCHITECTURAL MAPPING;
 * This class belongs to the Client User Manager component in the architectural diagram and maps directly
 * to the Friends class in the class diagram.
 *
 * Created by echo on 3/29/15.
 */
public class Friends extends Activity implements Invokable {
    /**
     * Called when the class receives an intent through a BroadcastReceiver
     *
     * @param intent
     */
    @Override
    public void onReceiveIntent(Intent intent) {

    }
}
