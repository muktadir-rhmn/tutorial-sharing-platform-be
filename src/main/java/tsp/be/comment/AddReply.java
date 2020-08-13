package tsp.be.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tsp.be.comment.models.CommentsRepository;
import tsp.be.user.UserDescriptor;

import static tsp.be.comment.MetaData.COMMENT_ROOT_PATH;

class AddReplyRequest {
	public String commentBody;
}

class AddReplyResponse {
	public String commentID;
}

@RestController
@RequestMapping(COMMENT_ROOT_PATH)
public class AddReply {
	@Autowired
	private CommentsRepository commentsRepository;

	@PostMapping("/{commentID}/add-reply")
	public AddReplyResponse addReply(
			@RequestAttribute("user") UserDescriptor userDescriptor,
			@PathVariable String commentID,
			@RequestBody AddReplyRequest request
	) {
		AddReplyResponse response = new AddReplyResponse();
		response.commentID = commentsRepository.addReply(userDescriptor.getUserID(), userDescriptor.getUserName(), commentID, request.commentBody);
		return response;
	}
}
