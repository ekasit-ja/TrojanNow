package UserManager;

import Interfaces.Invokable;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * PURPOSE:
 * Allows the user to log in to the system.
 *
 * OPERATION:
 * The Login class presents the user with a login screen which
 * lets the user log in to the system. This class takes input from the
 * user and sends out a "login attempt" intent with the user information.
 *
 * The login is successful once the server has confirmed that the credentials are valid.
 *
 * This class will listen for Intents with a BroadcastReceiver and send Intents via the
 * LocalBroadcastManager.
 *
 * ARCHITECTURAL MAPPING:
 * This class is part of the Client User Manager component in the architectural diagram and maps directly to
 * the Login class in the class diagram.
 *
 * Created by Eirik Skogstad.
 */
public class Login extends Activity implements Invokable {

    /**
     * Called when the class receives an intent through a BroadcastReceiver
     *
     * @param intent
     */
    @Override
    public void onReceiveIntent(Intent intent) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void login() {

    }
}
