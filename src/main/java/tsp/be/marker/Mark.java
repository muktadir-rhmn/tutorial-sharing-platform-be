package tsp.be.marker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tsp.be.marker.models.MarkingsRepository;
import tsp.be.user.UserDescriptor;
import tsp.be.utils.SingleMessageResponse;

import static tsp.be.marker.MetaData.PROGRESS_ROOT_PATH;

class MarkRequest {
	public String mark;
}

@RestController
@RequestMapping(PROGRESS_ROOT_PATH)
public class Mark {

	@Autowired
	private MarkingsRepository progressesRepository;

	@PostMapping("/{tutorialID}/{lessonID}")
	public SingleMessageResponse markAsDone(
			@RequestAttribute("user") UserDescriptor userDescriptor,
			@PathVariable String tutorialID,
			@PathVariable String lessonID,
			@RequestBody MarkRequest request
	){
		progressesRepository.mark(userDescriptor.getUserID(), tutorialID, lessonID, request.mark);

		return new SingleMessageResponse();
	}
}
