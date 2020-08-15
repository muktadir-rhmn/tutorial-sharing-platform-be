package tsp.be.marker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tsp.be.error.SingleMessageValidationException;
import tsp.be.marker.models.MarkingsRepository;
import tsp.be.user.UserDescriptor;

import static tsp.be.marker.MetaData.PROGRESS_ROOT_PATH;

class HasMarkRequest {
	public String mark;
}

class HasMarkResponse {
	public boolean hasMark;
}

@RestController
@RequestMapping(PROGRESS_ROOT_PATH)
public class HasMark {

	@Autowired
	private MarkingsRepository progressesRepository;

	@PostMapping("/{tutorialID}/{lessonID}/has-mark")
	public HasMarkResponse hasMark(
			@RequestAttribute("user") UserDescriptor userDescriptor,
			@PathVariable String tutorialID,
			@PathVariable String lessonID,
			@RequestBody HasMarkRequest request
	){
		validate(request);
		HasMarkResponse response = new HasMarkResponse();
		response.hasMark = progressesRepository.hasMark(userDescriptor.getUserID(), tutorialID, lessonID, request.mark);
		return response;
	}

	private void validate(HasMarkRequest request) {
		if (request.mark == null) throw new SingleMessageValidationException("Mark is required");
	}
}
