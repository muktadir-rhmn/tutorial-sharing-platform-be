package tsp.be.tutorial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tsp.be.tutorial.models.TutorialMetaData;
import tsp.be.tutorial.models.TutorialsRepository;
import tsp.be.user.auth.SigninNotRequired;

import java.util.List;

import static tsp.be.tutorial.MetaData.TUTORIAL_ROOT_PATH;

class GetTutorialsResponse {
	public List<TutorialMetaData> tutorials;
}

@RestController
public class GetTutorials {
	@Autowired
	private TutorialsRepository tutorialsRepository;

	@SigninNotRequired
	@GetMapping(TUTORIAL_ROOT_PATH)
	public GetTutorialsResponse getTutorials(
			@RequestParam(value = "categoryID", required = false) String categoryID,
			@RequestParam(value = "authorID", required = false) String authorID
	) {
		GetTutorialsResponse response = new GetTutorialsResponse();
		response.tutorials = tutorialsRepository.getTutorials(categoryID, authorID);
		return response;
	}

}
