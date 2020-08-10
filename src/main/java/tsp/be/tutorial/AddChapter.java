package tsp.be.tutorial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tsp.be.error.MappedValidationException;
import tsp.be.tutorial.models.TutorialsRepository;

import static tsp.be.tutorial.MetaData.TUTORIAL_ROOT_PATH;

class AddChapterRequest {
	public String name;
}

class AddChapterResponse {
	public String chapterID;
}

@RestController
public class AddChapter {

	@Autowired
	private TutorialsRepository tutorialsRepository;

	@PostMapping(TUTORIAL_ROOT_PATH + "/{tutorialID}/add-chapter")
	public AddChapterResponse addChapter(@PathVariable String tutorialID, @RequestBody AddChapterRequest request) {
		validate(tutorialID, request);
		String chapterID = tutorialsRepository.addChapter(tutorialID, request.name);

		AddChapterResponse response =  new AddChapterResponse();
		response.chapterID = chapterID;
		return response;
	}

	private void validate(String tutorialID, AddChapterRequest request) {
		MappedValidationException errors = new MappedValidationException();

		//todo: validate tutorialID exists
		if (request.name == null || request.name.equals("")) errors.addError("name", "You must provide a chapter name");

		errors.throwIfAnyError();
	}
}
