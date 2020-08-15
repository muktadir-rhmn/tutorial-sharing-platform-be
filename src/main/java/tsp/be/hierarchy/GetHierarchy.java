package tsp.be.hierarchy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;
import tsp.be.cache.CacheAPIResponse;
import tsp.be.cache.CacheObject;
import tsp.be.hierarchy.models.Category;
import tsp.be.hierarchy.models.HierarchyRepository;
import tsp.be.user.auth.SigninNotRequired;

@RestController
public class GetHierarchy {
	@Autowired
	private HierarchyRepository hierarchyRepository;

	@CacheAPIResponse
	@SigninNotRequired
	@GetMapping("/hierarchy")
	public Category getHierarchy(@RequestAttribute("cacheObject") CacheObject cacheObject) {
		Category category = hierarchyRepository.getHierarchy();
		cacheObject.setValue(category);
		return category;
	}
}
