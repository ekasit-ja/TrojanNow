package trojanow.profile;

import trojanow.interfaces.Invokable;
import android.app.Activity;
import android.content.Intent;

/**
 * PURPOSE:
 * This class allows the user to search for and view other users profiles.
 *
 * OPERATION:
 * This class displays a screen where the user can search for another user
 * and view her profile.
 *
 * ARCHITECTURAL MAPPING:
 * This class is part of the Profile component in the architectural diagram and maps directly
 * to the ProfileViewer class in the class diagram.
 *
 * Created by echo on 3/29/15.
 */
public class ProfileViewer extends Activity implements Invokable {

    /**
     * Called when the class receives an intent through a BroadcastReceiver
     */
    @Override
    public void onReceiveIntent(Intent intent) {

    }

    /**
     * Search for a users profile.
     */
    private void search() {

    }

    /**
     * Display the profile of a specific user.
     */
    private void displayProfile() {

    }
}
