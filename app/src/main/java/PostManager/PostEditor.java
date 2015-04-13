package PostManager;

import Interfaces.Invokable;
import android.app.Activity;
import android.content.Intent;

/**
 * PURPOSE:
 * This class enables the user to write posts and publish them globally or to a specific group,
 *
 * OPERATION:
 * The class shows the "edit post" screen which contains a text field for the post content and a submit button.
 * The user can also choose to publish the post to a specific group where she is a member, or globally.
 *
 * Once a user submits a post, an Intent is created and broadcast through LocalBroadcastManager.
 * The NetworkManager will listen to such intents and send them to the server.
 *
 * This class will also receive Intents from the SensorManager when a user wants to include sensor data in the
 * post.
 *
 * ARCHITECTURAL MAPPING;
 * This class belongs to the Client Post Manager component in the architectural diagram, and maps directly to
 * the PostEditor class in the class diagram.
 *
 * Created by Eirik Skogstad.
 */
public class PostEditor extends Activity implements Invokable {

    /**
     * Called when the class receives an intent through a BroadcastReceiver
     */
    @Override
    public void onReceiveIntent(Intent intent) {

    }

    /**
     * Publish a post to the system. This method will broadcast an Intent message with the new post information.
     */
    private void publishPost() {

    }

    /**
     * This method allows the user to request sensor information for the post such as temperature.
     */
    private void getSensorData() {

    }

    /**
     * Called after sensor data is received to include it in the post.
     */
    private void includeSensorData() {

    }
}
