package messenger;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class TestController {
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test(HttpServletRequest request, @CookieValue(name = "name", required = false) String name, HttpServletResponse response) {
        System.out.println("Got a request");
        System.out.println(name);
        response.addCookie(new Cookie("name", "muktadir"));
        int secs = 60;

        return "Sorry, I was sleeping";
    }
}
