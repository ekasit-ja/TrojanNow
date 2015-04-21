package usc.cs578.trojannow.manager.network;

/*
 * Created by Ekasit_Ja on 13-Apr-15.
 */
public class Url {
    public static final String host = "http://kanitsr.cloudapp.net/";
    public static final String getPostsByLocationApi = host + "/trojannow/getPostsByLocation/?location=%s";
    public static final String getPostAndCommentsApi = host + "/trojannow/getPostAndComments/?postId=%s";
}
