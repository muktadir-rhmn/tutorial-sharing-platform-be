package tsp.be.evaluation.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsp.be.tutorial.external.TutorialRatingUpdater;

@Service
public class EvaluationUpdateNotifier {
	private TutorialRatingUpdater tutorialRatingUpdater;
	@Autowired
	public EvaluationUpdateNotifier(TutorialRatingUpdater tutorialRatingUpdater) {
		this.tutorialRatingUpdater = tutorialRatingUpdater;
	}
	public void notifyAddEvaluation(String itemType, String itemID, String evaluation) {
		if (itemType.equals("tutorial")) {
			tutorialRatingUpdater.addRating(itemID, evaluation);
		}
	}
}
