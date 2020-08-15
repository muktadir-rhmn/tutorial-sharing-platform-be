package tsp.be.cache;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import tsp.be.utils.JsonConverter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class CacheInterceptor extends HandlerInterceptorAdapter {

	private Map<String, String> cache = new HashMap<>();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if(request.getMethod().equals("OPTIONS")) return true; //for CORS
		System.out.print("cache intercept: ");

		if (!shouldCache(handler)) return true;

		String cacheKey = generateKey(request);
		String cachedData = cache.get(cacheKey);
		if (cachedData == null) {
			request.setAttribute("cacheObject", new CacheObject(generateKey(request)));
			return true;
		}

		response.setContentType("application/json");
		response.getWriter().print(cachedData);

		System.out.println("serving data from cache");
		return false;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
		CacheObject cacheObject = (CacheObject) request.getAttribute("cacheObject");
		if (cacheObject == null) return;
		String cacheData = new JsonConverter().serialize(cacheObject.value);

		cache.put(cacheObject.key, cacheData);
	}

	private boolean shouldCache(Object handler) {
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		return handlerMethod.hasMethodAnnotation(CacheAPIResponse.class);
	}

	private String generateKey(HttpServletRequest request) {
		return request.getMethod() + ":" + request.getRequestURL();
	}
}
