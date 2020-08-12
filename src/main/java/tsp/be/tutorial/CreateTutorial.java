package tsp.be.tutorial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tsp.be.error.MappedValidationException;
import tsp.be.tutorial.models.TutorialsRepository;
import tsp.be.user.UserDescriptor;
import tsp.be.utils.Validator;

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

	@PostMapping(TUTORIAL_ROOT_PATH)
	public CreateTutorialResponse createTutorial(@RequestAttribute("user") UserDescriptor userDescriptor, @RequestBody CreateTutorialRequest request) {
		validate(request);
		String tutorialID = tutorialsRepository.createTutorial(userDescriptor.getUserID(), userDescriptor.getUserName(), request.name, request.description, request.categoryID);

		CreateTutorialResponse response = new CreateTutorialResponse();
		response.tutorialID = tutorialID;
		return response;
	}

	private void validate(CreateTutorialRequest request) {
		MappedValidationException errors = new MappedValidationException();

		if (Validator.isEmptyString(request.name)) errors.addError("name", "Name is required");
		if (Validator.isEmptyString(request.categoryID)) errors.addError("categoryID", "Category is required");
		//todo: check whether the categoryID exists or not

		errors.throwIfAnyError();
	}
}
