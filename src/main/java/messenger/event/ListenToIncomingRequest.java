package messenger.event;

import messenger.error.SimpleValidationException;
import messenger.user.UserDescriptor;
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
    private EventManager eventManager;

    @RequestMapping(value = "/listen", method = RequestMethod.POST)
    public DeferredResult<ListenResponse> listen(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @RequestBody ListenRequest listenRequest
    ) {
        validate(listenRequest);

        DeferredResult<ListenResponse> deferredResult = new DeferredResult<>();

        ListenResponse response = new ListenResponse();
        for (ListenableEventDescriptor descriptor: listenRequest.requestedEvents) {
            response.events.addAll(eventManager.getEventResponses(userDescriptor, listenRequest.lastEventTime, descriptor.eventType, descriptor.data));
        }
        if (response.events.size() > 0) {
            System.out.println("Event found. So, going to respond.");
            deferredResult.setResult(response);
        } else {
            System.out.println("No event found. So, going to listen");
            for (ListenableEventDescriptor descriptor: listenRequest.requestedEvents) {
                eventManager.addEventListener(deferredResult, userDescriptor, descriptor.eventType, descriptor.data);
            }
        }

        return deferredResult;
    }

    private void validate(ListenRequest listenRequest) {
        if (listenRequest.lastEventTime == null) throw new SimpleValidationException("Listener request must contain lastEventTime");
    }
}
