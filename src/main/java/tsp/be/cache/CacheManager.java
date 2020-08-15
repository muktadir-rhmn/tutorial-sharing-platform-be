package tsp.be.cache;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
class CacheManager {
	private Jedis jedis = new Jedis("localhost");

	public void set(String key, String value, int durationInSeconds) {
		jedis.set(key, value);
		jedis.expire(key, durationInSeconds);
	}

	public String get(String key) {
		return jedis.get(key);
	}
}
