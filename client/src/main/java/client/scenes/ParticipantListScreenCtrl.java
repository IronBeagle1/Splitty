package client.scenes;
import client.utils.ImageUtils;
import client.utils.ServerUtils;
import client.utils.Translation;
import com.google.inject.Inject;
import commons.Event;
import client.utils.Styling;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.*;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import static javafx.geometry.Pos.CENTER_LEFT;

public class ParticipantListScreenCtrl implements Initializable, SimpleRefreshable {
    @FXML
    private Button goBack;
    @FXML
    private ListView<HBox> participantList;
    private final ServerUtils server;
    private final Translation translation;
    private final MainCtrl mainCtrl;
    private final ImageUtils imageUtils;
    private final Map<Long, HBox> map;
    private final Styling styling;

    /**
     * Constructor for the participant list screen
     * @param mainCtrl the MainCtrl instance to use
     * @param server the ServerUtils instance to use
     * @param translation the Translation instance to use
     * @param imageUtils the ImageUtils instance to use
     * @param styling the Styling instance to use
     */
    @Inject
    public ParticipantListScreenCtrl(MainCtrl mainCtrl, ServerUtils server,
                                     Translation translation, ImageUtils imageUtils,
                                     Styling styling) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.translation = translation;
        this.imageUtils = imageUtils;
        this.map = new HashMap<>();
        this.styling = styling;
    }

    /**
     * Updates the event instance
     * @param event updated Event information
     */
    public void refresh(Event event) {
        refreshParticipantList(event);
    }

    /***
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
        prepareBackButton(goBack);
    }

    /***
     * Loads the back arrow image into the Go Back button
     * @param goBack the Go Back button
     */
    public void prepareBackButton(Button goBack){
        ImageView goBackImage = imageUtils.generateImageView("goBack.png", 15);
        goBack.setGraphic(goBackImage);
        styling.applyStyling(goBack, "positiveButton");
    }
    /**
     * Generates the participants in the list in the final form
     * @param event the Event data to use
     */
    public void refreshParticipantList(Event event) {
        participantList.getItems().clear();
        Image removeImage = imageUtils.loadImageFile("x_remove.png");
        for(Participant participant: event.getParticipants()) {
            HBox participantB1 = generateParticipantBox(participant.getId(), participant.getName());
            ImageView removeButton = generateRemoveButton(participant.getId(), event.getId(), removeImage);
            HBox participantBox = new HBox(removeButton, participantB1);
            participantBox.setAlignment(Pos.CENTER_RIGHT);
            participantBox.setStyle("-fx-border-style: none;");
            participantBox.setSpacing(10);
            map.put(participant.getId(), participantBox);
            participantBox.setAlignment(CENTER_LEFT);
            HBox.setHgrow(participantB1, Priority.ALWAYS);
            participantList.getItems().add(participantBox);
        }
    }

    /**
     * generates the remove button for each participant
     * @param participantId the participant for which the remove button is being generated
     * @param eventId the ID of the relevant event
     * @param removeImage the Image to place on the button
     * @return returns the symbol
     */
    public ImageView generateRemoveButton(long participantId, String eventId, Image removeImage) {
        ImageView imageView = imageUtils.generateImageView(removeImage, 15);
        imageView.setPickOnBounds(true);
        imageView.setOnMouseEntered(mouseEvent -> mainCtrl.getParticipantListScene().
                setCursor(Cursor.HAND));
        imageView.setOnMouseExited(mouseEvent -> mainCtrl.getParticipantListScene().
                setCursor(Cursor.DEFAULT));
        imageView.setOnMouseClicked(mouseEvent -> removeFromList(participantId, eventId));
        return imageView;
    }

    /**
     * Generates HBox for each participant
     * clicking the participant label leads to the add/edit screen of the participant
     * @param participantId id of the participant
     * @param name name of the participant
     * @return an HBox containing a label for a participant, and a remove button
     */
    public HBox generateParticipantBox(long participantId, String name) {
        HBox hbox = new HBox();
        Label participant = new Label(name);
        hbox.getChildren().add(participant);
        hbox.setOnMouseEntered(mouseEvent -> mainCtrl.getParticipantListScene()
                .setCursor(Cursor.HAND));
        hbox.setOnMouseExited(mouseEvent -> mainCtrl.getParticipantListScene()
                .setCursor(Cursor.DEFAULT));
        hbox.setOnMouseClicked(mouseEvent -> mainCtrl.switchToEditParticipant(participantId));
        return hbox;
    }

    /**
     * go back method when the button is clicked
     */
    public void goBack() {
        mainCtrl.switchScreens(EventScreenCtrl.class);
    }

    /**
     * Removes the participant from the list if deleted
     * @param participantId the ID of the participant
     * @param eventId the ID of the event
     */
    public void removeFromList(long participantId, String eventId){
        server.removeParticipant(eventId, participantId);
        HBox hBox = map.get(participantId);
        map.remove(participantId);
        participantList.getItems().remove(hBox);
    }

    /***
     * Loads the specified participant list into the controller
     * @param participantList the participant list used
     */
    public void loadParticipantList(ListView<HBox> participantList){
        this.participantList = participantList;
    }

}


