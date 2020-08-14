package tsp.be.tutorial.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsp.be.tutorial.models.TutorialsRepository;

@Service
public class TutorialRatingUpdater {
	private TutorialsRepository tutorialsRepository;

	@Autowired
	TutorialRatingUpdater(TutorialsRepository tutorialsRepository) {
		this.tutorialsRepository = tutorialsRepository;
	}
	public void addRating(String tutorialID, String rating) {
		tutorialsRepository.addRating(tutorialID, rating);
	}
}
