package tsp.be.user.models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsp.be.db.DatabaseManager;

@Service
public class UserRepository {
	private MongoCollection<Document> usersCollection;

	@Autowired
	public UserRepository(DatabaseManager databaseManager) {
		usersCollection = databaseManager.getCollection("users");
	}

	public void createUser(String name, String email, String password) {
		Document user = new Document();
		user.append("name", name);
		user.append("email", email);
		user.append("password", password);
		user.append("createdAt", System.currentTimeMillis());

		usersCollection.insertOne(user);
	}

	public User getUserByEmail(String email) {
		Document userDoc = usersCollection.find(Filters.eq("email", email)).first();
		if (userDoc == null) return null;

		User user = new User();
		user.id = userDoc.getObjectId("_id").toString();
		user.email = userDoc.getString("email");
		user.name = userDoc.getString("name");
		user.password = userDoc.getString("password");
		user.createdAt = userDoc.getLong("createdAt");

		return user;
	}
}
