package usc.cs578.trojannow.manager.post;

/**
 * PURPOSE:
 * This class represents a post in the system.
 *
 * ARCHITECTURAL MAPPING:
 * This class maps directly to the Post class in the class diagram.
 *
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
