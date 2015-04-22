package usc.cs578.trojannow.manager.network;

/*
 * Created by Ekasit_Ja on 13-Apr-15.
 */
public class Url {
    public static final String GET = "GET";
    public static final String POST = "POST";

    public static final String host = "http://kanitsr.cloudapp.net/";
    public static final String getPostsByLocationApi = host + "/trojannow/getPostsByLocation/?location=%s";
    public static final String getPostAndCommentsApi = host + "/trojannow/getPostAndComments/?postId=%s";
    public static final String registerUser = host + "/trojannow/registerUser/";
    public static final String forgotPassword = host + "/trojannow/forgotPassword/?email=%s";
    public static final String login = host + "/trojannow/login/";

    public static final String semicolon = ";";
    public static final String postSeparator = "&";
    public static final String postAssigner = "=";
    public static final String macAddressKey = "mac_address";

    public static final String emailKey = "email";
    public static final String passwordKey = "password";
    public static final String displayNameKey = "display_name";
    public static final String sessionIdKey = "session_id";

    public static final String statusKey = "status";
    public static final String errorMsgKey = "error_msg";
}
