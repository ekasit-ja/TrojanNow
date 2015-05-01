package usc.cs578.trojannow.manager.network;

/*
 * Created by Ekasit_Ja on 13-Apr-15.
 */
public class Url {
    public static final String GET = "GET";
    public static final String POST = "POST";

    public static final String host = "http://kanitsr.cloudapp.net/";
    //public static final String getPostsByLocationApi = host + "/trojannow/getPostsByLocation/?location=%s";
	public static final String getPostsByLocationApi = host + "/trojannow/getPostsByLocation/?latitude=%s&longitude=%s";
    public static final String getPostAndCommentsApi = host + "/trojannow/getPostAndComments/?postId=%s";

    public static final String getFriendsApi = host + "/trojannow/getFriends/";
    public static final String getUsersApi = host + "/trojannow/getUsers/?users=%s";
    public static final String updateFriendApi = host + "/trojannow/updateFriend/";
    public static final String sendMessageApi = host + "/trojannow/sendMessage/";
    public static final String getUnreadMessagesApi = host + "/trojannow/getUnreadMessages/?fromUser=%s";
	public static final String markUnreadMessages = host + "/trojannow/markUnreadMessages/?from_user=%s&to_user=%s&max_id=%s&min_id=%s";

    public static final String registerUser = host + "/trojannow/registerUser/";
    public static final String forgotPassword = host + "/trojannow/forgotPassword/?email=%s";
    public static final String login = host + "/trojannow/login/";
	public static final String logout = host + "/trojannow/logout/";
    public static final String createComment = host + "/trojannow/createComment/";
    public static final String createPost = host + "/trojannow/createPost/";
    public static final String ratePost = host + "/trojannow/ratePost/";
    public static final String rateComment = host + "/trojannow/rateComment/";

    public static final String updateAndroidRegistrationId = host + "/trojannow/updateAndroidRegistrationId/";

    public static final String semicolon = ";";
    public static final String postSeparator = "&";
    public static final String postAssigner = "=";
    public static final String macAddressKey = "mac_address";

    public static final String emailKey = "email";
    public static final String passwordKey = "password";
    public static final String displayNameKey = "display_name";
    public static final String sessionIdKey = "session_id";
    public static final String postIdKey = "post_id";
    public static final String commentTextKey = "comment_text";
	public static final String latitudeKey = "latitude";
	public static final String longitudeKey = "longitude";

    public static final String statusKey = "status";
    public static final String errorMsgKey = "error_msg";

    public static final String postTextKey = "post_text";
    public static final String showNameKey = "show_name";
    public static final String showLocationKey = "show_location";
    public static final String showTemptKey = "show_tempt";
    public static final String locationKey = "location";
    public static final String temptInCKey = "tempt_in_c";

    public static final String commentIdKey = "comment_id";
    public static final String ratingScoreKey = "rating_score";
    public static final String newScoreKey = "new_score";
	public static final String fromUserKey = "from_user";
	public static final String newCommentDataKey = "comment";

    public static final String fromUser = "from_user";
    public static final String toUser = "to_user";
    public static final String message = "message";
    public static final String action = "action";
    public static final String userId = "user_id";

    public static final String androidRegistrationIdKey = "android_registration_id";

    public static final String notificationTypeKey = "type";
    public static final String notificationContentKey = "content";
    public static final String got_comment_type = "got_comment";
	public static final String got_chat_message = "got_chat_message";

    public static final String SENDER_ID = "1026966500088"; // project number
    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String getProfileDataApi = host + "trojannow/getProfile/?user_id=%s";
    public static final String updateProfileApi = host + "trojannow/updateProfile/";
}
