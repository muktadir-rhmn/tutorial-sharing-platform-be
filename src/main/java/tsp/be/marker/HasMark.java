package tsp.be.marker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tsp.be.error.SingleMessageValidationException;
import tsp.be.marker.models.MarkingsRepository;
import tsp.be.user.UserDescriptor;

import static tsp.be.marker.MetaData.PROGRESS_ROOT_PATH;

class HasMarksRequest {
	public String[] marks;
}

class HasMarksResponse {
	public boolean[] hasMarks;
}

@RestController
@RequestMapping(PROGRESS_ROOT_PATH)
public class HasMark {

	@Autowired
	private MarkingsRepository progressesRepository;

	@PostMapping("/{tutorialID}/{lessonID}/has-mark")
	public HasMarksResponse hasMarks(
			@RequestAttribute("user") UserDescriptor userDescriptor,
			@PathVariable String tutorialID,
			@PathVariable String lessonID,
			@RequestBody HasMarksRequest request
	){
		validate(request);
		HasMarksResponse response = new HasMarksResponse();
		response.hasMarks = progressesRepository.hasMarks(userDescriptor.getUserID(), tutorialID, lessonID, request.marks);
		return response;
	}

	private void validate(HasMarksRequest request) {
		if (request.marks == null) throw new SingleMessageValidationException("Mark is required");
	}
}
