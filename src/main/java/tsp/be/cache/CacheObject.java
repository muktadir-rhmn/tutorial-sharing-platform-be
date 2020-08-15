package tsp.be.cache;

public class CacheObject {
	String key;
	Object value;
	int durationInSec;

	CacheObject(String key) {
		this.key = key;
	}

	public CacheObject(String key, int durationInSec) {
		this.key = key;
		this.durationInSec = durationInSec;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String toString() {
		return "Key: " + key + " value: " + value;
	}
}
