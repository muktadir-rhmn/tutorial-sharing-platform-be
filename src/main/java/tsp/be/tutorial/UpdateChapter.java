package tsp.be.tutorial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tsp.be.error.MappedValidationException;
import tsp.be.tutorial.models.TutorialsRepository;
import tsp.be.utils.SingleMessageResponse;
import tsp.be.utils.Validator;

import static tsp.be.tutorial.MetaData.TUTORIAL_ROOT_PATH;

class UpdateChapterRequest {
	public String name;
}

@RestController
@RequestMapping(TUTORIAL_ROOT_PATH)
public class UpdateChapter {
	@Autowired
	private TutorialsRepository tutorialsRepository;

	@PostMapping("/{tutorialID}/{chapterID}")
	public SingleMessageResponse updateChapter(
			@PathVariable String tutorialID,
			@PathVariable String chapterID,
			@RequestBody UpdateChapterRequest request
	){
		validate(request);
		tutorialsRepository.updateChapter(tutorialID, chapterID, request.name);

		return new SingleMessageResponse("Operation successful");
	}

	private void validate(UpdateChapterRequest request) {
		MappedValidationException exception = new MappedValidationException();
		if (Validator.isEmptyString(request.name)) exception.addError("name", "Invalid name");
		exception.throwIfAnyError();
	}

}
