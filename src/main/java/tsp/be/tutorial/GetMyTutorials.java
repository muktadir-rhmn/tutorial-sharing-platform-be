package tsp.be.tutorial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tsp.be.tutorial.models.TutorialsRepository;
import tsp.be.user.UserDescriptor;

import static tsp.be.tutorial.MetaData.TUTORIAL_ROOT_PATH;

@RestController
@RequestMapping(TUTORIAL_ROOT_PATH)
public class GetMyTutorials {
	@Autowired
	private TutorialsRepository tutorialsRepository;

	@GetMapping("/my")
	public GetTutorialsResponse getTutorials(@RequestAttribute("user") UserDescriptor userDescriptor) {
		GetTutorialsResponse response = new GetTutorialsResponse();
		response.tutorials = tutorialsRepository.getTutorialsOfUser(userDescriptor.getUserID());
		return response;
	}

}
