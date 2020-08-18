package tsp.be.user.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import tsp.be.user.UserDescriptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class AuthInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    private TokenManager tokenManager = TokenManager.getInstance();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(request.getMethod().equals("OPTIONS")) return true; //for CORS

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        logger.info(request.getMethod() + " " + request.getRequestURL());
        if (handlerMethod.hasMethodAnnotation(SigninNotRequired.class)) return true;

        String token = request.getHeader("token");

        UserDescriptor userDescriptor = tokenManager.verifyTokenAndDecodeData(token);

        if (userDescriptor == null) {
            addErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Sign in required to access this api");
            return false;
        }

        boolean hasAccess = !handlerMethod.hasMethodAnnotation(RequireAccess.class) ||
                userDescriptor.hasAccess(handlerMethod.getMethodAnnotation(RequireAccess.class).value());
        if (!hasAccess) {
            addErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Permission denied");
            return false;
        }

        request.setAttribute("user", userDescriptor);
        return true;
    }

    private void addErrorResponse(HttpServletResponse response, int statusCode, String message) {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        try {
            response.getWriter().printf("{\"message\":\"%s\"}", message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}