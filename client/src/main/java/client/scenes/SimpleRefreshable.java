package client.scenes;

import commons.Event;

public interface SimpleRefreshable {
    /***
     * Refreshes the contents of a controller
     * @param event the new Event data to process
     */
    void refresh(Event event);
}
