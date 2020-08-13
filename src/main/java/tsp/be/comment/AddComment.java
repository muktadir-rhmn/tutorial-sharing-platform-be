package tsp.be.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tsp.be.comment.models.CommentsRepository;
import tsp.be.user.UserDescriptor;

import static tsp.be.comment.MetaData.COMMENT_ROOT_PATH;

class AddCommentRequest {
	public String lessonID;
	public String commentBody;
}

class AddCommentResponse {
	public String commentID;
}

@RestController
@RequestMapping(COMMENT_ROOT_PATH)
public class AddComment {

	@Autowired
	private CommentsRepository commentsRepository;

	@PostMapping("")
	public AddCommentResponse addComment(@RequestAttribute("user") UserDescriptor userDescriptor, @RequestBody AddCommentRequest request) {
		AddCommentResponse response = new AddCommentResponse();
		response.commentID = commentsRepository.addComment(userDescriptor.getUserID(), userDescriptor.getUserName(), request.lessonID, request.commentBody);
		return response;
	}

}
