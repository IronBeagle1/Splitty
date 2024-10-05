package client.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebSocketUtilsTest {

    @Test
    void getLoggingSessionHandlerAdapter() {
        final boolean[] callbackRan = {false};
        WebSocketUtils sut = new WebSocketUtils("/test");
        var adapter = sut.getLoggingSessionHandlerAdapter(()->callbackRan[0] = true);
        assertNotNull(adapter);
        adapter.handleException(null, null, null, null, new Exception());
        adapter.handleTransportError(null, new Exception());
        assertTrue(callbackRan[0]);
    }
}