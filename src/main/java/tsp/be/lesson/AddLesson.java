package tsp.be.lesson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tsp.be.error.exceptions.MappedValidationException;
import tsp.be.lesson.models.LessonsRepository;
import tsp.be.utils.Validator;

import static tsp.be.lesson.MetaData.LESSON_ROOT_PATH;

class AddLessonRequest {
	public String tutorialID;
	public String chapterID;
	public String name;
	public String body;
}

class AddLessonResponse {
	public String lessonID;
}

@RestController
public class AddLesson {
	@Autowired
	private LessonsRepository lessonsRepository;

	@PostMapping(LESSON_ROOT_PATH)
	public AddLessonResponse addLesson(@RequestBody AddLessonRequest request) {
		validate(request);

		String lessonID = lessonsRepository.addLesson(request.tutorialID, request.chapterID, request.name, request.body);

		AddLessonResponse response = new AddLessonResponse();
		response.lessonID = lessonID;
		return response;
	}

	private void validate(AddLessonRequest request) {
		MappedValidationException exception = new MappedValidationException();

		if (Validator.isEmptyString(request.name)) exception.addError("name", "Name field is mandatory");
		if (Validator.isEmptyString(request.body)) exception.addError("body", "Body field is mandatory");
		if (Validator.isEmptyString(request.tutorialID)) exception.addError("tutorialID", "Tutorial is mandatory");
		if (Validator.isEmptyString(request.chapterID)) exception.addError("chapterID", "Chapter is mandatory");

		exception.throwIfAnyError();
	}
}
