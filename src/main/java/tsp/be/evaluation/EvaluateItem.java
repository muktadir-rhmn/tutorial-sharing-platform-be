package tsp.be.evaluation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tsp.be.evaluation.models.EvaluationsRepository;
import tsp.be.user.UserDescriptor;
import tsp.be.utils.SingleMessageResponse;

import static tsp.be.evaluation.MetaData.EVALUATION_ROOT_PATH;

class EvaluateItemRequest {
	public String evaluation;
	public String itemType; //for notifying appropriate module to update the evaluation
}

@RestController
@RequestMapping(EVALUATION_ROOT_PATH)
public class EvaluateItem {

	@Autowired
	private EvaluationsRepository evaluationsRepository;

	@PostMapping("/{itemID}")
	public SingleMessageResponse rateItem(
			@RequestAttribute("user") UserDescriptor userDescriptor,
			@PathVariable String itemID,
			@RequestBody EvaluateItemRequest request
	) {
		evaluationsRepository.addEvaluation(userDescriptor.getUserID(), itemID, request.evaluation, request.itemType);
		return new SingleMessageResponse("Operation Success");
	}
}
