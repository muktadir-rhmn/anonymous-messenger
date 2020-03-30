package messenger.messaging;

import messenger.event.EventManager;
import messenger.event.events.TypingEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class SetTyping {
    @Autowired
    private EventManager eventManager;

    @RequestMapping(value = "/threads/{threadID}/typing", method = RequestMethod.POST)
    public String setTyping(
            @RequestAttribute("userType") String userType,
            @RequestAttribute("userID") Long userID,
            @PathVariable Long threadID
    ) {
        eventManager.newEvent(new TypingEvent(userType, userID, threadID));
        return "";
    }
}
