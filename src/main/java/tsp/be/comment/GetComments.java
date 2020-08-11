package tsp.be.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tsp.be.comment.models.Comment;
import tsp.be.comment.models.CommentsRepository;
import tsp.be.user.auth.SigninNotRequired;

import java.util.List;

import static tsp.be.comment.MetaData.COMMENT_ROOT_PATH;

class GetCommentsResponse {
	public List<Comment> comments;
	public int nTotalComments;
}

@RestController
@RequestMapping(COMMENT_ROOT_PATH)
public class GetComments {

	@Autowired
	private CommentsRepository commentsRepository;

	@SigninNotRequired
	@GetMapping("/{lessonID}")
	public GetCommentsResponse getComments(
			@PathVariable String lessonID,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize
	) {
		GetCommentsResponse response = new GetCommentsResponse();

		response.comments = commentsRepository.getComments(lessonID, pageNo, pageSize);
		response.nTotalComments = commentsRepository.countTotalComments(lessonID);

		return response;
	}
}
