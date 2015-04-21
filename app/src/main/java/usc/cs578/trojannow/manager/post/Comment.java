package usc.cs578.trojannow.manager.post;

/*
 * Created by Ekasit_Ja on 14-Apr-15.
 */
public class Comment {
    protected int id;
    protected int post_id;
    protected String commentatorName;
    protected String commentText;
    protected String commentTimestamp;
    protected int commentScore;

    public Comment(int id, int post_id, String commentatorName,
                   String commentText,String commentTimestamp, int commentScore) {
        this.id = id;
        this.post_id = post_id;
        this.commentatorName = commentatorName;
        this.commentText = commentText;
        this.commentTimestamp = commentTimestamp;
        this.commentScore = commentScore;
    }
}
