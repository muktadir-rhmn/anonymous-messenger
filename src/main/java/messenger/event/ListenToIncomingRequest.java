package messenger.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class ListenableEventDescriptor {
    public Integer eventType;
    public Map<String, Object> data;
}

class ListenRequest {
    public Long lastEventTime;
    public List<ListenableEventDescriptor> requestedEvents = new LinkedList<>();
}

class ListenResponse {
    public List<Object> events = new LinkedList<>();
}

@RestController
public class ListenToIncomingRequest {
    private final static long LISTEN_TIME_OUT_MILLIS = 60000;

    @Autowired
    private EventProcessor eventProcessor;

    @Autowired
    private EventManager eventManager;

    @RequestMapping(value = "/listen", method = RequestMethod.POST)
    public DeferredResult<ListenResponse> listen(
            @RequestAttribute(value = "userID") Long userID,
            @RequestAttribute(value = "threadID", required = false) Long threadID,
            @RequestBody ListenRequest listenRequest
    ) {
        DeferredResult<ListenResponse> deferredResult = new DeferredResult<>();

        ListenResponse response = new ListenResponse();
        for (ListenableEventDescriptor descriptor: listenRequest.requestedEvents) {
            response.events.addAll(eventManager.getEventResponses(userID, threadID, listenRequest.lastEventTime, descriptor.eventType, descriptor.data));
        }
        if (response.events.size() > 0) {
            System.out.println("Event found. So, going to respond.");
            deferredResult.setResult(response);
        } else {
            System.out.println("No event found. So, going to listen");
            for (ListenableEventDescriptor descriptor: listenRequest.requestedEvents) {
                eventProcessor.addEventListener(userID, threadID, descriptor.eventType, deferredResult, descriptor.data);
            }
        }

        return deferredResult;
    }
}
