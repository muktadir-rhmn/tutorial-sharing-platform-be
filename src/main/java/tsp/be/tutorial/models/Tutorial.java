package tsp.be.tutorial.models;

import java.util.ArrayList;
import java.util.List;

public class Tutorial {
	public String id;
	public String name;
	public String description;

	public String authorID;
	public String authorName;

	public List<Chapter> chapters;

}

class Chapter {
	public String id;
	public String name;

	public List<LessonMetaData> lessons = new ArrayList<>();
}