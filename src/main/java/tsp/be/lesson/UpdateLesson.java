package tsp.be.lesson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tsp.be.error.MappedValidationException;
import tsp.be.lesson.models.LessonsRepository;
import tsp.be.utils.SimpleResponse;
import tsp.be.utils.Validator;

import static tsp.be.lesson.MetaData.LESSON_ROOT_PATH;

class UpdateLessonRequest {
	public String name;
	public String body;
}

@RestController
public class UpdateLesson {
	@Autowired
	private LessonsRepository lessonsRepository;

	@PutMapping(LESSON_ROOT_PATH + "/{lessonID}")
	public SimpleResponse updateLesson(@PathVariable String lessonID, @RequestBody UpdateLessonRequest request) {
		validate(request);

		lessonsRepository.updateLesson(lessonID, request.name, request.body);

		return new SimpleResponse("Update successful");
	}

	private void validate(UpdateLessonRequest request) {
		MappedValidationException exception = new MappedValidationException();

		if (Validator.isEmptyString(request.name)) exception.addError("name", "Name field is required");
		if (Validator.isEmptyString(request.body)) exception.addError("body", "Body field is required");

		exception.throwIfAnyError();
	}

}
