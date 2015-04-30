package usc.cs578.trojannow.manager.network;

/*
 * Created by Ekasit_Ja on 15-Apr-15.
 */
public class Method {
    public static final String PREF_NAME = "TrojanNow-settings";
    public static final String TEMPT_UNITS = "temptUnits";
    public static final int FAHRENHEIT = 0;
    public static final int CELSIUS = 1;

    public static final String methodKey = "method";
    public static final String parameterKey = "parameter";
    public static final String postIdKey = "postId";
    public static final String locationKey = "location";
    public static final String statusKey = "status";
    public static final String resultKey = "result";
    public static final String emailKey = "email";

    public static final String usersKey = "users";
    public static final String userIdKey = "userId";

    public static final String getPostsByLocation = "getPostsByLocation";
    public static final String getPostAndComments = "getPostAndComments";

    public static final String getFriends = "getFriends";
    public static final String sendMessage = "sendMessage";
    public static final String getUnreadMessages = "getUnreadMessages";

    public static final String registerUser = "registerUser";
    public static final String forgotPassword = "forgotPassword";
    public static final String login = "login";
	public static final String logout = "logout";
    public static final String loginSuccess = "loginSuccess";
    public static final String registerSuccess = "registerSuccess";
    public static final String logoutSuccess = "logoutSuccess";
    public static final String loginAfterRegister = "loginAfterRegister";
    public static final String createPost = "createPost";
    public static final String createComment = "createComment";
    public static final String changeTemptUnit = "changeTemptUnit";
    public static final String ratePost = "ratePost";
    public static final String ratePostFromComment = "ratePostFromComment";
    public static final String rateComment = "rateComment";
    public static final String refreshCommentViewer = "refreshCommentViewer";
    public static final String refreshPostViewer = "refreshPostViewer";

    public static final String getUsers = "getUsers";
    public static final String updateFriend = "updateFriend";

    public static final String updateAndroidRegistrationId = "updateAndroidRegistrationId";
    public static final String gotComment = "gotComment";

    public static final String fromNotificationKey = "fromNotification";
    public static final String fromNotificationMethodKey = "fromNotificationMethodKey";
    public static final String scrollBottomKey = "scrollBottom";

    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String getProfileData = "getProfileData";
    public static final String profileData = "profileData";
}
