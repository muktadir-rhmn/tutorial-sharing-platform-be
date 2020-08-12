package tsp.be.tutorial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tsp.be.tutorial.models.TutorialsRepository;
import tsp.be.user.auth.SigninNotRequired;

import static tsp.be.tutorial.MetaData.TUTORIAL_ROOT_PATH;


@RestController
@RequestMapping(TUTORIAL_ROOT_PATH)
public class GetRecentTutorials {
	@Autowired
	private TutorialsRepository tutorialsRepository;

	@SigninNotRequired
	@GetMapping("/recent")
	public GetTutorialsResponse getTutorials() {
		GetTutorialsResponse response = new GetTutorialsResponse();
		response.tutorials = tutorialsRepository.getRecentTutorials(5);
		return response;
	}

}
