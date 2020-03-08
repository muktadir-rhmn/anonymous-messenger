package messenger.auth;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class AuthInterceptor extends HandlerInterceptorAdapter {
    private TokenManager tokenManager = TokenManager.getInstance();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(request.getMethod().equals("OPTIONS")) return true;
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        if (handlerMethod.hasMethodAnnotation(SigninNotRequired.class)) return true;

        String token = null;
        Cookie[] cookies = request.getCookies();
        for (int i = 0; i < cookies.length; i++) {
            if (cookies[i].getName().equals("token")){
                token = cookies[i].getValue();
                break;
            }
        }
        if (token == null) return false;

        boolean isValidToken = tokenManager.verifyTokenAndSetRequestAttr(token, request);

        return isValidToken;
    }
}