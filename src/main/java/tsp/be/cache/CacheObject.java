package tsp.be.cache;

public class CacheObject {
	String key;
	Object value;

	CacheObject(String key) {
		this.key = key;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String toString() {
		return "Key: " + key + " value: " + value;
	}
}
