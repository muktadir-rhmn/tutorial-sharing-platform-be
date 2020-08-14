package tsp.be.comment.models;

import java.util.List;

public class Comment {
	public String id;
	public String lessonID;
	public String commenterID;
	public String commenterName;
	public String body;
	public Integer nLikes;
	public List<Reply> replies;
	public Long createdAt;
	public Long updatedAt;
}
