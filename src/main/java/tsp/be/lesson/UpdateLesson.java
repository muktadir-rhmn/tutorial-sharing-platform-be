package tsp.be.lesson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tsp.be.error.MappedValidationException;
import tsp.be.lesson.models.LessonsRepository;
import tsp.be.utils.SingleMessageResponse;
import tsp.be.utils.Validator;

import static tsp.be.lesson.MetaData.LESSON_ROOT_PATH;

class UpdateLessonRequest {
	public String name;
	public String body;
}

@RestController
@RequestMapping(LESSON_ROOT_PATH)
public class UpdateLesson {
	@Autowired
	private LessonsRepository lessonsRepository;

	@PostMapping("/{lessonID}")
	public SingleMessageResponse updateLesson(@PathVariable String lessonID, @RequestBody UpdateLessonRequest request) {
		validate(request);

		lessonsRepository.updateLesson(lessonID, request.name, request.body);

		return new SingleMessageResponse("Update successful");
	}

	private void validate(UpdateLessonRequest request) {
		MappedValidationException exception = new MappedValidationException();

		if (Validator.isEmptyString(request.name)) exception.addError("name", "Name field is required");
		if (Validator.isEmptyString(request.body)) exception.addError("body", "Body field is required");

		exception.throwIfAnyError();
	}

}
