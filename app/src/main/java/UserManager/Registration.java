package UserManager;

import Interfaces.Invokable;
import android.app.Activity;
import android.content.Intent;

/**
 * PURPOSE:
 * This class allows the user to register in the system.
 *
 * OPERATION:
 * This class displays a registration screen where the user inputs her username and password.
 *
 * The credentials will be broadcast as an intent which will be picked up by NetworkManager
 * and sent to the server.
 *
 * ARCHITECTURAL MAPPING:
 * This class is part of the Client User Manager component in the architectural diagram and maps directly to the
 * Registration class in the class diagram.
 *
 * Created by Eirik Skogstad.
 */
public class Registration extends Activity implements Invokable {
    /**
     * Called when the class receives an intent through a BroadcastReceiver
     *
     * @param intent
     */
    @Override
    public void onReceiveIntent(Intent intent) {

    }
}
