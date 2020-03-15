package messenger.event;

import messenger.auth.TokenManager;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

class EventListenerDescriptor {
    public DeferredResult<ListenResponse> deferredResult;

    public Integer eventType;
    public String userType;
    public long userID;
    public Long threadID;
    public Map<String, Object> data;

    public EventListenerDescriptor(DeferredResult<ListenResponse> deferredResult, String userType, Long userID, Long threadID, Integer eventType, Map<String, Object> data) {
        this.eventType = eventType;
        this.userType = userType;
        this.userID = userID;
        this.threadID = threadID;
        this.deferredResult = deferredResult;
        this.data = data;
    }

}

@Service
public class EventProcessor implements Runnable{
    private ConcurrentLinkedQueue<Event> queue = new ConcurrentLinkedQueue<>();
    private ConcurrentHashMap<Integer, ConcurrentLinkedQueue<EventListenerDescriptor>> listeners = new ConcurrentHashMap<>(); //todo: move this to event manager

    public EventProcessor() {
        Thread thread = new Thread(this);
        thread.start();

        Collection<Integer> eventTypes = EventManager.eventTypes();
        for (Integer eventType : eventTypes) {
            listeners.put(eventType, new ConcurrentLinkedQueue<>());
        }

    }

    public synchronized void enqueueEvent(Event event) {
        queue.add(event);
        System.out.println("Enqueued event and waking up processor");
        notifyAll();
    }

    public synchronized void addEventListener(String userType, Long userID, Long threadID, Integer eventType, DeferredResult<ListenResponse> deferredResult, Map<String, Object> data) {
        listeners.get(eventType).add(new EventListenerDescriptor(deferredResult, userType, userID, threadID, eventType, data));
    }

    private synchronized void processEvent() {
        while (queue.size() == 0) {
            try {
                System.out.println("EventProcessor: no event found. so going to sleep");
                wait();
                System.out.println("EventProcessor: waking up from sleep");
            } catch (InterruptedException e) {
                System.out.println("EventProcessor: interrupted from sleep");
                e.printStackTrace();
            }
        }

        Event event = queue.poll();
        System.out.println("EventProcessor: Event found in event queue: " + event.getClass().getSimpleName());


        int eventType = event.getEventType();
        ConcurrentLinkedQueue<EventListenerDescriptor> eventListeners = listeners.get(eventType);
        ConcurrentLinkedQueue<EventListenerDescriptor> tmpListeners = new ConcurrentLinkedQueue<>();
        while (!eventListeners.isEmpty()){
            EventListenerDescriptor eventListenerDescriptor = eventListeners.poll();

            if (eventListenerDescriptor.deferredResult.isSetOrExpired()) continue;

            if (!isListenersEvent(eventListenerDescriptor, event)) {
                tmpListeners.add(eventListenerDescriptor);
                continue;
            }

            Object response = EventManager.getInstance().getResponseGenerator(eventType).generateResponseData(event.eventDescriptor);

            ListenResponse listenResponse = new ListenResponse();
            listenResponse.events.add(response);
            eventListenerDescriptor.deferredResult.setResult(listenResponse);
        }

        eventListeners.addAll(tmpListeners);
    }

    private boolean isListenersEvent(EventListenerDescriptor eventListenerDescriptor, Event event) {
        if(eventListenerDescriptor.userType.equals(TokenManager.USER_TYPE_SINGED_IN)) {
            return eventListenerDescriptor.userID == event.eventDescriptor.userID;
        } else if (eventListenerDescriptor.userType.equals(TokenManager.USER_TYPE_INITIATOR)){
            return eventListenerDescriptor.threadID.equals(event.eventDescriptor.threadID);
        } return false;
    }

    @Override
    public void run() {
        System.out.println("Starting Event processor thread");
        while (true) {
            System.out.println("Going to process one event");
            processEvent();
        }
    }
}
