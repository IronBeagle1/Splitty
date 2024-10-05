package client.utils;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import commons.dto.EventNameChangeDTO;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import javafx.application.Platform;
import org.glassfish.jersey.client.ClientConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class LPUtils {
    private final String serverURL;
    private final ExecutorService exec = Executors.newSingleThreadExecutor();

    /***
     * Constructor for Long Polling Utils
     * @param serverURL the server URL to use
     */
    @Inject
    public LPUtils(@Named("connection.URL") String serverURL) {
        this.serverURL = serverURL;
    }

    /**
     * Gets the event from the server based on the invite code
     * @param consumer the Consumer to execute when a name update occurs
     */
    public void registerForNameUpdates(Consumer<EventNameChangeDTO> consumer){
        exec.submit(()-> {
            while(!Thread.interrupted()){
                Response res = ClientBuilder.newClient(new ClientConfig())
                        .target(serverURL).path("api/updates/names") //invite code is the ID
                        .request(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .get(Response.class);

                if(res.getStatus() == 204) continue;

                EventNameChangeDTO dto = res.readEntity(EventNameChangeDTO.class);
                Platform.runLater(() -> consumer.accept(dto));
            }
        });
    }

    /***
     * Terminates the long polling background thread
     */
    public void stopLP(){
        exec.shutdownNow();
    }
}
