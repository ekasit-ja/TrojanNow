package trojanow.profile;

import android.app.Activity;
import android.content.Intent;
import trojanow.interfaces.Invokable;

/**
 * PURPOSE:
 * This class lets the user update her profile information so that other users can see it.
 *
 * OPERATION:
 * This class displays a screen where the user can update her profile information.
 *
 * Profile updates are sent via the LocalBroadcastManager and stored in the server,
 *
 * ARCHITECTURAL MAPPING:
 * This class is part of the Profile component in the architectural diagram and maps directly
 * to the ProfileEditor class in the class diagram.
 *
 * Created by Eirik Skogstad.
 */
public class ProfileEditor extends Activity implements Invokable {

    /**
     * Called when the class receives an intent through a BroadcastReceiver
     */
    @Override
    public void onReceiveIntent(Intent intent) {

    }

    /**
     * Creates and broadcasts an "update profile" Intent with the information specified
     * by the user.
     */
    private void updateProfile() {

    }
}
