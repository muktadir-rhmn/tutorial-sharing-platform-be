package tsp.be.hierarchy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tsp.be.hierarchy.models.HierarchyRepository;

import static tsp.be.hierarchy.MetaData.HIERARCHY_ROOT_PATH;

class CreateCategoryRequest {
	public String name;
	public String[] pathFromRoot;
}

class CreateCategoryResponse {
	public String categoryID;
}

@RestController
@RequestMapping(HIERARCHY_ROOT_PATH)
public class CreateCategory {

	@Autowired
	private HierarchyRepository hierarchyRepository;

	@PostMapping("/create-category")
	public CreateCategoryResponse createCategory(@RequestBody CreateCategoryRequest request) {
		validate(request);

		CreateCategoryResponse response = new CreateCategoryResponse();
		response.categoryID = hierarchyRepository.createCategory(request.name, request.pathFromRoot);
		return response;
	}

	private void validate(CreateCategoryRequest request) {
	}
}
