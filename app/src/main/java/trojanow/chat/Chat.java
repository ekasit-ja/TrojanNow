package trojanow.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import trojanow.interfaces.Invokable;
import trojanow.usermanager.User;

/**
 * PURPOSE:
 * This class will implement the functionality required to let users send instant messages to their
 * friends.
 *
 * OPERATION:
 * This class is responsible for displaying the chat screen and handling user
 * input on this screen. The screen will allow the user to select a friend to chat with,
 * see chat messages and write chat messages.
 *
 * This class will contain a BroadcastReceiver which will register to the LocalBroadcastManager
 * to receive chat messages from other users. com.usc.trojanow.Chat messages from the current user will be distributed
 * by the LocalBroadcastManager to other components.
 *
 * Since all chat communication goes through the server, the NetworkManager will be responsible
 * for passing messages to the server and distributing the messages coming from the server.
 *
 * ARCHITECTURAL MAPPING:
 * The com.usc.trojanow.Chat class maps directly to the com.usc.trojanow.Chat Client component in the architectural diagram and the
 * ChatClient class in the class diagram.
 *
 * Created by Eirik Skogstad.
 */
public class Chat extends Activity implements Invokable {

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
        setContentView(R.layout.activity_main);
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
     * Initiate a chat session. This will send a request to start a new chat session.
     */
    private void startChat() {

    }

    /**
     * End a chat session. This will send a request to end the current chat session.
     */
    private void endChat() {

    }

    /**
     * Send a chat message. This method is called after the user has typed a message
     * and clicked "send". A new Intent will be created containing the message and distributed by
     * LocalBroadcastManager.
     */
    private void sendMessage() {

    }

    /**
     * Handle an incoming chat message. This method is invoked when the BroadcastReceiver receives
     * a new chat message Intent. The message is extracted from the Intent and passed to displayMessage.
     */
    private void receiveMessage() {

    }

    /**
     * Display a new chat message on the screen.
     *
     * @param message The incoming chat message.
     * @param fromUser The user from whom the message originated.
     */
    private void displayMessage(String message, User fromUser) {

    }
}
