package client.utils;

import client.scenes.SimpleRefreshable;
import commons.Event;

import java.util.ArrayList;
import java.util.List;

public class TestRefreshable implements SimpleRefreshable {
    private Event currentEvent = null;
    private final List<Event> eventsRefreshed = new ArrayList<>();

    /**
     * Refresh the refreshable with a new event
     * @param event the new Event data to process
     */
    @Override
    public void refresh(Event event) {
        currentEvent = event;
        eventsRefreshed.add(event);
    }

    /**
     * Get all the events have been updated or refreshed.
     * @return All refreshed events
     */
    public List<Event> getEventsRefreshed() {
        return eventsRefreshed;
    }

    /**
     * Get the current event
     * @return Current event
     */
    public Event getCurrentEvent() {
        return currentEvent;
    }
}
