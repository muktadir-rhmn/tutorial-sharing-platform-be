package tsp.be.user.auth;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import tsp.be.error.AccessDeniedException;
import tsp.be.error.SingleMessageValidationException;
import tsp.be.user.UserDescriptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class AuthInterceptor extends HandlerInterceptorAdapter {
    private TokenManager tokenManager = TokenManager.getInstance();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(request.getMethod().equals("OPTIONS")) return true; //for CORS

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        System.out.println(request.getMethod() + " " + request.getRequestURL());
        if (handlerMethod.hasMethodAnnotation(SigninNotRequired.class)) return true;

        String token = request.getHeader("token");

        UserDescriptor userDescriptor = tokenManager.verifyTokenAndDecodeData(token);

        if (userDescriptor == null) throw new AccessDeniedException();

        boolean hasAccess = handlerMethod.hasMethodAnnotation(RequireAccess.class) &&
                userDescriptor.hasAccess(handlerMethod.getMethodAnnotation(RequireAccess.class).value());
        if (!hasAccess) throw new AccessDeniedException();

        request.setAttribute("user", userDescriptor);
        return true;
    }
}