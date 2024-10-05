package client.scenes;

import client.utils.ServerUtils;
import client.utils.Translation;
import com.google.inject.Inject;
import commons.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class EditTitleCtrl implements Initializable, SimpleRefreshable {
    @FXML
    private TextField title;
    @FXML
    private Label label;
    @FXML
    private Button confirm;
    @FXML
    private Button cancel;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final Translation translation;
    private Event event;

    /**
     *
     * @param mainCtrl the main controller
     * @param server the server
     * @param translation the translation class
     */
    @Inject
    public EditTitleCtrl(MainCtrl mainCtrl, ServerUtils server, Translation translation) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.translation = translation;
    }

    /**
     *
     * @param location
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.label.textProperty().bind(translation.getStringBinding("editTitle.label"));
        this.title.promptTextProperty().bind(translation.getStringBinding("editTitle.promptText"));
        this.cancel.textProperty().bind(translation.getStringBinding("editTitle.cancelButton"));
        this.confirm.textProperty().bind(translation.getStringBinding("editTitle.confirmButton"));
    }

    /**
     *
     * @param event the new Event data to process
     */
    public void refresh(Event event) {
        this.event = event;
    }

    /**
     * updates the information on the server with the new title
     * that was provided
     */
    public void confirm() {
        server.editTitle(event.getId(), title.getText());
        title.clear();
        mainCtrl.switchScreens(EventScreenCtrl.class);
    }

    /**
     * switches back to the event screen
     */
    public void cancel() {
        title.clear();
        mainCtrl.switchScreens(EventScreenCtrl.class);
    }
}
