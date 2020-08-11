package tsp.be.lesson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tsp.be.lesson.models.Lesson;
import tsp.be.lesson.models.LessonsRepository;

import static tsp.be.lesson.MetaData.LESSON_ROOT_PATH;

@RestController
@RequestMapping(LESSON_ROOT_PATH)
public class GetLesson {
	@Autowired
	private LessonsRepository lessonsRepository;

	@GetMapping("/{lessonID}")
	public Lesson getLesson(@PathVariable String lessonID) {
		return lessonsRepository.getLesson(lessonID);
	}
}
