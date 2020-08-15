package tsp.be.cache;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;

@Service
class CacheManager {
	private Jedis jedis = new Jedis("localhost");

	public void set(String key, String value, int durationInSeconds) {
		try{
			Pipeline pipeline = jedis.pipelined();
			pipeline.set(key, value);
			pipeline.expire(key, durationInSeconds);
			pipeline.sync();
			jedis.resetState();
		} catch (JedisConnectionException ex) {
			ex.printStackTrace();
			throw new CacheServerDownException();
		}
	}

	public String get(String key) {
		try{
			return jedis.get(key);
		} catch (JedisConnectionException ex) {
			ex.printStackTrace();
			throw new CacheServerDownException();
		}
	}
}
