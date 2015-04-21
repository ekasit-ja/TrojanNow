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

    public Post(int id, String posterName, String postText,
                String location, String postTimestamp, int postScore) {
        this.id = id;
        this.posterName = posterName;
        this.postText = postText;
        this.location = location;
        this.postTimestamp = postTimestamp;
        this.postScore = postScore;
    }

}
