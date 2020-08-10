package tsp.be.tutorial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tsp.be.error.MappedValidationException;
import tsp.be.tutorial.models.TutorialsRepository;
import tsp.be.user.UserDescriptor;

import static tsp.be.tutorial.MetaData.TUTORIAL_ROOT_PATH;

class CreateTutorialRequest {
	public String name;
	public String description;
	public String categoryID;
}

class CreateTutorialResponse {
	public String tutorialID;
}

@RestController
public class CreateTutorial {

	@Autowired
	private TutorialsRepository tutorialsRepository;

	@PostMapping(TUTORIAL_ROOT_PATH + "/create")
	public CreateTutorialResponse createTutorial(@RequestAttribute("user") UserDescriptor userDescriptor, @RequestBody CreateTutorialRequest request) {
		validate(request);
		String tutorialID = tutorialsRepository.createTutorial(userDescriptor.getUserID(), userDescriptor.getUserName(), request.name, request.description, request.categoryID);

		CreateTutorialResponse response = new CreateTutorialResponse();
		response.tutorialID = tutorialID;
		return response;
	}

	private void validate(CreateTutorialRequest request) {
		MappedValidationException errors = new MappedValidationException();

		if (request.name == null || request.name.equals("")) errors.addError("name", "You must enter a name");
		if (request.categoryID == null || request.categoryID.equals("") ) errors.addError("categoryID", "You must select a category");
		//todo: check whether the categoryID exists or not

		errors.throwIfAnyError();
	}
}
