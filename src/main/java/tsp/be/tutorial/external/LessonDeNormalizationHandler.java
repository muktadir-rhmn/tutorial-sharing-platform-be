package tsp.be.tutorial.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsp.be.tutorial.models.TutorialsRepository;

@Service
public class LessonDeNormalizationHandler {

	@Autowired
	private TutorialsRepository tutorialsRepository;

	/** Handles de-normalization*/
	public void addLesson(String tutorialID, String chapterID, String lessonID, String name) {
		tutorialsRepository.addLesson(tutorialID, chapterID, lessonID, name);
	}
}
