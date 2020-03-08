package messenger.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


public class AuthInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private TokenManager tokenManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
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

        boolean isValid = tokenManager.verifyTokenAndSetRequestAttr(token, request);
        if (!isValid) return false;

        return true;
    }
}