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
    public List<ListenableEventDescriptor> requestedEvents;
}

class ListenResponse {
    public List<Object> eventResponse = new LinkedList<>();
}

@RestController
public class ListenToIncomingEvent {
    private final static long LISTEN_TIME_OUT_MILLIS = 60000;

    @Autowired
    private EventProcessor eventProcessor;

    @Autowired
    private EventManager eventManager;

    @RequestMapping(value = "/listen", method = RequestMethod.POST)
    public DeferredResult<ListenResponse> listen(
            @RequestAttribute("userID") Long userID,
            @RequestAttribute("threadID") Long threadID,
            @RequestBody ListenRequest listenRequest
    ) {
        DeferredResult<ListenResponse> deferredResult = new DeferredResult<>(LISTEN_TIME_OUT_MILLIS);

        ListenResponse response = new ListenResponse();
        for (ListenableEventDescriptor descriptor: listenRequest.requestedEvents) {
            response.eventResponse.addAll(eventManager.getEventResponses(userID, threadID, descriptor.data));
        }
        if (response.eventResponse.size() > 0) {
            deferredResult.setResult(response);
        }

        for (ListenableEventDescriptor descriptor: listenRequest.requestedEvents) {
            eventProcessor.addEventListener(userID, threadID, descriptor.eventType, deferredResult, descriptor.data);
        }

        return deferredResult;
    }
}
