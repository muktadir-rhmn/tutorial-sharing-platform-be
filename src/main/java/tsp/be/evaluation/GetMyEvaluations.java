package tsp.be.evaluation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tsp.be.evaluation.models.EvaluationsRepository;
import tsp.be.user.UserDescriptor;

import java.util.List;

import static tsp.be.evaluation.MetaData.EVALUATION_ROOT_PATH;

class GetMyEvaluationsRequest {
	public List<String> itemIDs;
}

class GetMyEvaluationsResponse {
	public List<String> evaluations;
}

@RestController
@RequestMapping(EVALUATION_ROOT_PATH)
public class GetMyEvaluations {
	@Autowired
	private EvaluationsRepository evaluationsRepository;

	/** why POST even though it's a GET request?
	 * We need to receive an array of itemIDs.
	 *
	 * GET should not have a body. [ref: https://stackoverflow.com/questions/978061/http-get-with-request-body]
	 *
	 * We can send an array in query parameter like: https://stackoverflow.com/questions/3061273/send-an-array-with-an-http-get
	 * But this is not any standard method. Also, it will repeat the same field name as many times as the number array element and so wastage of bandwidth
	 * */
	@PostMapping("/my")
	public GetMyEvaluationsResponse getMyEvaluations(
			@RequestAttribute("user") UserDescriptor userDescriptor,
			@RequestBody GetMyEvaluationsRequest request
	) {
		GetMyEvaluationsResponse response = new GetMyEvaluationsResponse();

		response.evaluations = evaluationsRepository.getEvaluations(userDescriptor.getUserID(), request.itemIDs);

		return response;
	}
}
