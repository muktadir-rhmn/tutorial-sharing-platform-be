package tsp.be.tutorial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tsp.be.error.exceptions.MappedValidationException;
import tsp.be.tutorial.models.TutorialsRepository;
import tsp.be.utils.SingleMessageResponse;
import tsp.be.utils.Validator;

import static tsp.be.tutorial.MetaData.TUTORIAL_ROOT_PATH;

class UpdateTutorialRequest {
	public String name;
	public String description;
	public String categoryID;
}

@RestController
@RequestMapping(TUTORIAL_ROOT_PATH)
public class UpdateTutorial {
	@Autowired
	private TutorialsRepository tutorialsRepository;

	@PostMapping("/{tutorialID}")
	public SingleMessageResponse updateTutorial(
			@PathVariable String tutorialID,
			@RequestBody UpdateTutorialRequest request
	){
		validate(request);
		tutorialsRepository.updateTutorial(tutorialID, request.categoryID, request.name, request.description);

		return new SingleMessageResponse("Operation successful");
	}

	private void validate(UpdateTutorialRequest request) {
		MappedValidationException exception = new MappedValidationException();
		if (Validator.isEmptyString(request.name)) exception.addError("name", "Invalid name");
		if (Validator.isEmptyString(request.description)) exception.addError("description", "Invalid description");
		exception.throwIfAnyError();
	}

}
