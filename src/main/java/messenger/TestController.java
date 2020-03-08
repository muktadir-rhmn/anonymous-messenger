package messenger;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class TestController {
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test(HttpServletRequest request,HttpServletResponse response, @RequestAttribute("userType")String tokenType) {
        System.out.println("Got a request");
//        String tokenType = (String) request.getAttribute("tokenType");
        System.out.println(tokenType);
        return "Sorry, I was sleeping";
    }
}
