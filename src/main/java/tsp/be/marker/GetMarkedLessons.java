package tsp.be.marker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tsp.be.marker.models.MarkingsRepository;
import tsp.be.user.UserDescriptor;

import java.util.List;

import static tsp.be.marker.MetaData.PROGRESS_ROOT_PATH;

class GetMarkedLessonsResponse {
	public List<String> lessonIDs;
}

@RestController
@RequestMapping(PROGRESS_ROOT_PATH)
public class GetMarkedLessons {

	@Autowired
	private MarkingsRepository progressesRepository;

	@GetMapping("/{tutorialID}/{mark}")
	public GetMarkedLessonsResponse getMarkedLessons(
			@RequestAttribute("user") UserDescriptor userDescriptor,
			@PathVariable String tutorialID,
			@PathVariable String mark
	) {
		GetMarkedLessonsResponse response = new GetMarkedLessonsResponse();
		response.lessonIDs = progressesRepository.getMarkedLessons(userDescriptor.getUserID(), tutorialID, mark);
		return response;
	}
}
