package tsp.be.evaluation.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsp.be.comment.external.CommentLikeDislikeUpdater;
import tsp.be.tutorial.external.TutorialRatingUpdater;

@Service
public class EvaluationUpdateNotifier {
	private TutorialRatingUpdater tutorialRatingUpdater;
	private CommentLikeDislikeUpdater commentLikeDislikeUpdater;

	@Autowired
	public EvaluationUpdateNotifier(
			TutorialRatingUpdater tutorialRatingUpdater,
			CommentLikeDislikeUpdater commentLikeDislikeUpdater
	) {
		this.tutorialRatingUpdater = tutorialRatingUpdater;
		this.commentLikeDislikeUpdater = commentLikeDislikeUpdater;
	}
	public void notifyAddEvaluation(String itemType, String itemID, String evaluation) {
		if (itemType.equals("tutorial")) {
			tutorialRatingUpdater.addRating(itemID, evaluation);
		} else if (itemType.equals("comment")) {
			commentLikeDislikeUpdater.addLikeDislike(itemID, evaluation);
		}
	}
}
