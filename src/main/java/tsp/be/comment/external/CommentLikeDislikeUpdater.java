package tsp.be.comment.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsp.be.comment.models.CommentsRepository;

@Service
public class CommentLikeDislikeUpdater {
	private CommentsRepository commentsRepository;

	@Autowired
	CommentLikeDislikeUpdater(CommentsRepository commentsRepository) {
		this.commentsRepository = commentsRepository;
	}

	public void addLikeDislike(String commentIDPath, String evaluation) {

		commentsRepository.addLikeDislike(commentIDPath.split(":"), evaluation);
	}
}
