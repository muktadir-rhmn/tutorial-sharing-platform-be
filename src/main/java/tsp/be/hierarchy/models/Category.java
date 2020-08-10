package tsp.be.hierarchy.models;

import java.util.ArrayList;
import java.util.List;

public class Category {
	public String id;
	public String name;
	public List<Category> children = new ArrayList<>();
}
