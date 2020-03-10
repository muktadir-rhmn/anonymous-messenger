package messenger.event;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

class EventListenerDescriptor {
    public DeferredResult<ListenResponse> deferredResult;

    public Integer eventType;
    public Long userID;
    public Long threadID;
    public Map<String, Object> data;

    public EventListenerDescriptor(DeferredResult<ListenResponse> deferredResult, Long userID, Long threadID, Integer eventType, Map<String, Object> data) {
        this.eventType = eventType;
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

        listeners.put(0, new ConcurrentLinkedQueue<>()); //for new message event
    }

    public synchronized void enqueueEvent(Event event) {
        queue.add(event);
        System.out.println("Enqueued event and waking up processor");
        notifyAll();
    }

    public synchronized void addEventListener(Long userID, Long threadID, Integer eventType, DeferredResult<ListenResponse> deferredResult, Map<String, Object> data) {
        listeners.get(eventType).add(new EventListenerDescriptor(deferredResult, userID, threadID, eventType, data));
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
        while (!eventListeners.isEmpty()){
            EventListenerDescriptor descriptor = eventListeners.poll();
            if (descriptor.deferredResult.isSetOrExpired()) continue;

            if (descriptor.threadID != null && !descriptor.threadID.equals(event.threadID)) continue;
            if (descriptor.userID != null && !descriptor.userID.equals(event.userID)) continue;

            EventDescriptor eventDescriptor = new EventDescriptor();
            event.threadID = descriptor.threadID;
            event.userID = descriptor.userID;
            Object response = event.generateResponseData(eventDescriptor, descriptor.data);

            ListenResponse listenResponse = new ListenResponse();
            listenResponse.events.add(response);
            descriptor.deferredResult.setResult(listenResponse);
        }
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
