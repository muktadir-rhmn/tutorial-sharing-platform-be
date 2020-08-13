package tsp.be.hierarchy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tsp.be.hierarchy.models.HierarchyRepository;
import tsp.be.utils.SingleMessageResponse;

import static tsp.be.hierarchy.MetaData.HIERARCHY_ROOT_PATH;

class UpdateCategoryRequest {
	public String[] idPath;
	public String name;
	public String[] parentIDPath;
}

@RestController
@RequestMapping(HIERARCHY_ROOT_PATH)
public class UpdateCategory {
	@Autowired
	private HierarchyRepository hierarchyRepository;

	@PostMapping("/update-category")
	public SingleMessageResponse updateCategory(@RequestBody UpdateCategoryRequest request) {
		hierarchyRepository.updateCategory(request.idPath, request.name, request.parentIDPath);

		return new SingleMessageResponse("Operation Success");
	}
}
