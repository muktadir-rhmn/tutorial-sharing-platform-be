package tsp.be.cache;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;
import tsp.be.config.ConfigurationManager;
import tsp.be.config.pojos.RedisConfiguration;

@Service
class CacheManager {
	private RedisConfiguration redisConfiguration;
	private Jedis jedis;

	public CacheManager() {
		redisConfiguration = ConfigurationManager.getRedisConfiguration();

		jedis = new Jedis(redisConfiguration.host, redisConfiguration.port);
	}

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
