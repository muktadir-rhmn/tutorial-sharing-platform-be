package tsp.be.marker.models;

import org.junit.jupiter.api.BeforeEach;
import tsp.be.db.DatabaseManager;

class MarkingsRepositoryTest {
	private MarkingsRepository markingsRepository;

	@BeforeEach
	public void beforeAll() {
		DatabaseManager databaseManager = new DatabaseManager();

		markingsRepository = new MarkingsRepository(databaseManager);
	}


}