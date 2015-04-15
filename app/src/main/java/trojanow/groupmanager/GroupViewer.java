package trojanow.groupmanager;

import trojanow.interfaces.Invokable;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * PURPOSE:
 * Displays details about a specific group and lists its members, and will
 * let the user invite new members to the group or remove members from the group.
 *
 * OPERATION:
 * This class will present a screen where the user can see the list of members in a
 * particular group.
 *
 * Updates to a group will be sent to other components through the LocalBroadcastManager,
 * and information about groups will be received through a BroadcastReceiver.
 *
 * ARCHITECTURAL MAPPING:
 * This class is part of the Group Manager component in the architectural diagram and maps
 * directly to the GroupViewer class in the class diagram.
 *
 * Created by Eirik Skogstad.
 */
public class GroupViewer extends Activity implements Invokable {

    /**
     * Called when the class receives an intent through a BroadcastReceiver
     */
    @Override
    public void onReceiveIntent(Intent intent) {

    }

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Called after {@link #onCreate} &mdash; or after {@link #onRestart} when
     * the activity had been stopped, but is now again being displayed to the
     * user.
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Called when you are no longer visible to the user.
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Perform any final cleanup before an activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Display a specific group on the screen.
     */
    private void displayGroup(){

    }

    /**
     * Invite a user to a particular group.
     */
    private void inviteUser(){

    }

    /**
     * Remove a user from a particular group.
     * This can only be done by the user who is the group owner.
     */
    private void removeUser(){

    }
}
