package usc.cs578.trojannow.manager.post;

/*
 * Created by Ekasit_Ja on 13-Apr-15.
 */
public class Post {
    protected int id;
    protected String posterName;
    protected String postText;
    protected String location;
    protected String postTimestamp;
    protected int postScore;
    protected int replyCount;
    protected int userRating;
    protected String tempt_in_c;

    public Post(int id, String posterName, String postText, String location, String postTimestamp,
                int postScore, int replyCount, int userRating, String tempt_in_c) {
        this.id = id;
        this.posterName = posterName;
        this.postText = postText;
        this.location = location;
        this.postTimestamp = postTimestamp;
        this.postScore = postScore;
        this.replyCount = replyCount;
        this.userRating = userRating;
        this.tempt_in_c = tempt_in_c;
    }

}
