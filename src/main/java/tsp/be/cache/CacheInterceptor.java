package tsp.be.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import tsp.be.utils.JsonConverter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CacheInterceptor extends HandlerInterceptorAdapter {
	Logger logger = LoggerFactory.getLogger(CacheInterceptor.class);

	private CacheManager cacheManager;

	public CacheInterceptor() {
		this.cacheManager = new CacheManager();
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		if (!handlerMethod.hasMethodAnnotation(CacheAPIResponse.class)) return true;

		String cacheKey = generateKey(request);
		request.setAttribute("cacheObject", new CacheObject(generateKey(request), handlerMethod.getMethodAnnotation(CacheAPIResponse.class).durationInSec()));

		String cachedData;
		try{
			cachedData = cacheManager.get(cacheKey);
		} catch (CacheServerDownException ex) {
			/** if the Cache Server is down, caching will simply ignored and execution will be done as if there is no cache at all */
			logger.error("Cache Server down");
			return true;
		}

		if (cachedData == null) return true;

		response.setContentType("application/json");
		response.getWriter().print(cachedData);

		logger.info("serving data from cache");
		return false;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
		CacheObject cacheObject = (CacheObject) request.getAttribute("cacheObject");
		if (cacheObject == null) return;
		String cacheData = new JsonConverter().serialize(cacheObject.value);

		try{
			cacheManager.set(cacheObject.key, cacheData, cacheObject.durationInSec);
		} catch (CacheServerDownException exception) {
			/** if the Cache Server is down, caching will simply ignored and execution will be done as if there is no cache at all */
			logger.error("Cache Server down");
		}

	}

	private String generateKey(HttpServletRequest request) {
		return request.getMethod() + ":" + request.getRequestURL();
	}
}
