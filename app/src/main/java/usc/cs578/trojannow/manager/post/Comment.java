package usc.cs578.trojannow.manager.post;

/*
 * Created by Ekasit_Ja on 14-Apr-15.
 */
public class Comment {
    protected int id;
    protected int post_id;
    protected String commentText;
    protected String commentTimestamp;
    protected int commentScore;
    protected int commentRating;

    public Comment(int id, int post_id, String commentText,
                   String commentTimestamp, int commentScore, int commentRating) {
        this.id = id;
        this.post_id = post_id;
        this.commentText = commentText;
        this.commentTimestamp = commentTimestamp;
        this.commentScore = commentScore;
        this.commentRating = commentRating;
    }
}
