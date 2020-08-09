package tsp.be.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.stereotype.Component;
import tsp.be.config.ConfigurationManager;
import tsp.be.config.pojos.DatabaseConfiguration;

@Component
public class DatabaseManager {
    private MongoDatabase database;

    public DatabaseManager() {
        DatabaseConfiguration configuration = ConfigurationManager.getDatabaseConfiguration();

        MongoClient mongoClient = MongoClients.create();

        database = mongoClient.getDatabase(configuration.databaseName);
    }

    public MongoCollection<Document> getCollection(String collectionName) {
        return database.getCollection(collectionName);
    }
}
