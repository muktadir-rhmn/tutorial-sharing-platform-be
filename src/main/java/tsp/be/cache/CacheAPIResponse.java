package tsp.be.cache;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CacheAPIResponse {
	int durationInSec() default 5 * 60;
}
