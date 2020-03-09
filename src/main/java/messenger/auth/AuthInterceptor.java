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

        System.out.println(request.getMethod() + " " + request.getRequestURL());
        if (handlerMethod.hasMethodAnnotation(SigninNotRequired.class)) return true;

        String token = null;
        token = request.getHeader("token");
//        Cookie[] cookies = request.getCookies();
//        if (cookies == null) {
//            System.out.println("no cookies found. so not logged in");
//            return false;
//        }
//        for (int i = 0; i < cookies.length; i++) {
//            if (cookies[i].getName().equals("token")){
//                token = cookies[i].getValue();
//                break;
//            }
//        }
        if (token == null) {
            System.out.println("No token found in cookies, so not logged in");
            return false;
        }

        boolean isValidToken = tokenManager.verifyTokenAndSetRequestAttr(token, request);

        return isValidToken;
    }
}