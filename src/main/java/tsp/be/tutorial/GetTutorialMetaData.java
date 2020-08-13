package tsp.be.tutorial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import tsp.be.tutorial.models.Tutorial;
import tsp.be.tutorial.models.TutorialMetaData;
import tsp.be.tutorial.models.TutorialsRepository;
import tsp.be.user.auth.SigninNotRequired;

import static tsp.be.tutorial.MetaData.TUTORIAL_ROOT_PATH;

@RestController
public class GetTutorialMetaData {
	@Autowired
	private TutorialsRepository tutorialsRepository;

	@SigninNotRequired
	@GetMapping(TUTORIAL_ROOT_PATH + "/{tutorialID}/meta-data")
	public TutorialMetaData getTutorialMetaData(@PathVariable String tutorialID) {
		return tutorialsRepository.getTutorialMetaData(tutorialID);
	}
}
