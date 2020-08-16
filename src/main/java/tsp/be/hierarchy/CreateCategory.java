package tsp.be.hierarchy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tsp.be.hierarchy.models.HierarchyRepository;
import tsp.be.user.UserDescriptor;
import tsp.be.user.auth.RequireAccess;

import static tsp.be.hierarchy.MetaData.HIERARCHY_ROOT_PATH;

class CreateCategoryRequest {
	public String name;
	public String[] parentIDPath;
}

class CreateCategoryResponse {
	public String categoryID;
}

@RestController
@RequestMapping(HIERARCHY_ROOT_PATH)
public class CreateCategory {

	@Autowired
	private HierarchyRepository hierarchyRepository;

	@RequireAccess(UserDescriptor.USER_TYPE_ADMIN)
	@PostMapping("/create-category")
	public CreateCategoryResponse createCategory(@RequestAttribute("user") UserDescriptor userDescriptor, @RequestBody CreateCategoryRequest request) {
		validate(request);

		CreateCategoryResponse response = new CreateCategoryResponse();
		response.categoryID = hierarchyRepository.createCategory(request.name, request.parentIDPath);
		return response;
	}

	private void validate(CreateCategoryRequest request) {
	}
}
