package trojanow.postmanager;

import android.app.Activity;
import android.content.Intent;
import trojanow.interfaces.Invokable;

/**
 * PURPOSE:
 * This class enables the user to view posts from other users. A user can see posts that are posted
 * in the same area where the user is, or see posts that belong to a particular group.
 *
 * OPERATION:
 * This class presents a screen which lists posts either from a specific group or those that are
 * publicly posted in the same area.
 *
 * ARCHITECTURAL MAPPING:
 * This class is part of the Client Post Manager component in the architectural diagram and maps directly to
 * the PostViewer class in the class diagram.
 *
 * Created by Eirik Skogstad.
 */
public class PostViewer extends Activity implements Invokable {

    /**
     * Called when the class receives an intent through a BroadcastReceiver
     */
    @Override
    public void onReceiveIntent(Intent intent) {

    }

    /**
     * List posts, either public posts or posts in a specific group.
     */
   public void listPosts() {

   }
}
