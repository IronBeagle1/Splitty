package server.api;

import commons.dto.EventNameChangeDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Controller
@RequestMapping("/api/updates")
public class LPController {
    private final Map<Object, Consumer<EventNameChangeDTO>> listeners = new ConcurrentHashMap<>();

    /***
     * Long Polling endpoint for name updates
     * @return a ResponseEntity containing a DTO if there is a name change within the time span
     * A NO_CONTENT response otherwise
     */
    @GetMapping("/names")
    public DeferredResult<ResponseEntity<EventNameChangeDTO>> getNameUpdates(){
        ResponseEntity<Object> noContent = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        DeferredResult<ResponseEntity<EventNameChangeDTO>> res =
                new DeferredResult<>(5000L, noContent);

        Object key = new Object();
        listeners.put(key, dto -> res.setResult(ResponseEntity.ok(dto)));
        res.onCompletion(()->listeners.remove(key));

        return res;
    }

    /***
     * Propagates an event name change to all listeners
     * @param dto the EventNameChangeDTO to send out
     */
    public void propagateToAllListeners(EventNameChangeDTO dto){
        listeners.forEach((key, listener) -> listener.accept(dto));
    }
}
