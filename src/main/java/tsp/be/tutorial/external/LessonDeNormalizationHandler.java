package tsp.be.tutorial.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsp.be.tutorial.models.TutorialsRepository;

@Service
public class LessonDeNormalizationHandler {
	/** Handles de-normalization of lesson name*/

	@Autowired
	private TutorialsRepository tutorialsRepository;

	public void addLesson(String tutorialID, String chapterID, String lessonID, String name) {
		tutorialsRepository.addLesson(tutorialID, chapterID, lessonID, name);
	}

	public void updateLesson(String tutorialID, String chapterID, String lessonID, String name) {
		tutorialsRepository.updateLesson(tutorialID, chapterID, lessonID, name);
	}
}
