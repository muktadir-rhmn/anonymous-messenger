package messenger;

import messenger.error.SimpleValidationException;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class TestController {
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test(HttpServletRequest request,HttpServletResponse response, @RequestAttribute("userType")String tokenType) {
        System.out.println("Got a request");
//        String tokenType = (String) request.getAttribute("tokenType");
        System.out.println(tokenType);
        throw new SimpleValidationException("sorry, testing validation error");
//        return "Sorry, I was sleeping";
    }
}
