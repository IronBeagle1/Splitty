package client.scenes;

import client.utils.*;
import com.google.inject.Inject;
import commons.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.*;
import java.util.ResourceBundle;

public class DeleteEventsScreenCtrl implements Initializable {
    @FXML
    private Label selectWhichEventsToDelete;
    @FXML
    private ListView<Event> checkEventsListView;
    @FXML
    private Button deleteSelectedEvenetsButton;
    @FXML
    private Label noEventsSelectedLabel;
    @FXML
    private Button deleteAllEventsButton;
    @FXML
    private Button goBackButton;
    private final Map<Event, Boolean> eventSelectionMap = new HashMap<>();
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final Translation translation;
    private final ManagementOverviewUtils utils;
    private final ImageUtils imageUtils;
    private final StringGenerationUtils stringUtils;
    private boolean listWasInitialized = false;
    private Styling styling;
    /**
     * Constructor
     *
     * @param server      the ServerUtils instance to use
     * @param mainCtrl    the MainCtrl instance to use
     * @param translation the Translation instance to use
     * @param utils       the ManagementOverviewUtils instance to use
     * @param imageUtils  the ImageUtils instance to use
     * @param styling     the Styling instance to use
     * @param stringUtils the StringUtils instance to use
     */
    @Inject
    public DeleteEventsScreenCtrl(ServerUtils server, MainCtrl mainCtrl, Translation translation,
                                  ManagementOverviewUtils utils, ImageUtils imageUtils,
                                  StringGenerationUtils stringUtils, Styling styling) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.translation = translation;
        this.utils = utils;
        this.imageUtils = imageUtils;
        this.styling = styling;
        this.stringUtils = stringUtils;
    }

    /**
     * Initialize basic features for the Management Overview Screen
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
        selectWhichEventsToDelete.textProperty().bind(translation.getStringBinding("DES.Select.Delete"));
        deleteSelectedEvenetsButton.textProperty().bind(translation.getStringBinding("DES.Delete.Selected.Events.Button"));
        deleteAllEventsButton.textProperty().bind(translation.getStringBinding("DES.Delete.All.Events"));
        ImageView goBackImage = imageUtils.generateImageView("goBack.png", 15);
        goBackButton.setGraphic(goBackImage);
    }

    /**
     * initialize an event list view with checkboxes
     * keep the selected events in a hash-map
     */
    public void initializeEventsCheckList() {
        if(listWasInitialized) return;
        checkEventsListView.setItems(utils.retrieveEvents());
        checkEventsListView.setCellFactory(listView -> new ListCell<>() {
            private final CheckBox checkBox = new CheckBox();
            @Override
            protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                if (empty || event == null || event.getId() == null) {
                    textProperty().bind(translation.getStringBinding("empty"));
                    setGraphic(null);
                } else {
                    textProperty().bind(stringUtils.generateTextForEventLabel(event));
                    checkBox.setOnAction(e -> eventSelectionMap.put(event, checkBox.isSelected()));
                    if (!eventSelectionMap.containsKey(event)) {
                        eventSelectionMap.put(event, false);
                    }
                    checkBox.setSelected(eventSelectionMap.get(event));
                    setGraphic(checkBox);
                }
            }
        });
        listWasInitialized = true;
    }

    /**
     * delete all the selected events
     * popup for checking whether the admin wants to proceed or not
     */
    public void deleteSelectedEvents() {
        if(!eventSelectionMap.containsValue(true)){
            noEventsSelectedLabel.textProperty().bind(translation.getStringBinding("DES.No.Events.Selected.Label"));
            styling.changeStyling(noEventsSelectedLabel, "successText", "errorText");
            System.out.println("No events selected");
        }
        else {
            // The dialog segments do not have textProperties, so here we are, fetching Strings...
            String confirmationTitle = translation.getStringBinding("DES.Confirm.Title").getValue();
            String confirmationHeader = translation.getStringBinding("DES.Confirm.Header").getValue();
            String confirmationContent = translation.getStringBinding("DES.Confirm.Content").getValue();

            ButtonType buttonTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            Alert confirmationDialog = generateConfirmationDialog(confirmationTitle, confirmationHeader, confirmationContent, buttonTypeYes);
            Optional<ButtonType> result = confirmationDialog.showAndWait();
            if (result.isPresent() && result.get() == buttonTypeYes) {
                List<Event> selectedEvents = eventSelectionMap.entrySet().stream()
                        .filter(Map.Entry::getValue)
                        .map(Map.Entry::getKey)
                        .toList();
                checkEventsListView.getItems().removeAll(selectedEvents);
                for (Event current : selectedEvents) {
                    server.deleteEvent(current.getId());
                    System.out.println("The following event has been deleted: " + current.getTitle());
                }
                noEventsSelectedLabel.textProperty().bind(translation.getStringBinding("DES.Event.Deleted.Sucessfully"));
                styling.changeStyling(noEventsSelectedLabel, "errorText", "successText");
                eventSelectionMap.clear();
            } else {
                noEventsSelectedLabel.textProperty().bind(translation.getStringBinding("DES.Event.Deletion.Cancel"));
                System.out.println("Deletion cancelled.");
                styling.changeStyling(noEventsSelectedLabel, "successText", "errorText");
            }
        }
    }

    /**
     * delete all the existent events from the database
     * popup for confirmation of the action
     * on click delete all the events
     */
    public void deleteAllEvents() {
        String confirmationTitle = translation.getStringBinding("DES.ConfirmAll.Title").getValue();
        String confirmationHeader = translation.getStringBinding("DES.ConfirmAll.Header").getValue();
        String confirmationContent = translation.getStringBinding("DES.ConfirmAll.Content").getValue();

        ButtonType buttonTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        Alert confirmationDialog = generateConfirmationDialog(confirmationTitle, confirmationHeader, confirmationContent, buttonTypeYes);
        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeYes) {
            server.deleteAllEvents();
            checkEventsListView.getItems().clear();
            eventSelectionMap.clear();
            System.out.println("Everything was deleted successfully!");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    /***
     * Generates a confirmation dialog with a given title, header, and content
     * @param confirmationTitle the window title to use
     * @param confirmationHeader the dialog header to use
     * @param confirmationContent the dialog content to use
     * @param buttonTypeYes the ButtonType of the Yes option
     * @return a Alert with the corresponding details
     */
    public Alert generateConfirmationDialog(String confirmationTitle, String confirmationHeader,
                                            String confirmationContent, ButtonType buttonTypeYes){
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle(confirmationTitle);
        confirmationDialog.setHeaderText(confirmationHeader);
        confirmationDialog.setContentText(confirmationContent);

        ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        confirmationDialog.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        return confirmationDialog;
    }

    /**
     * go to the management overview screen
     * on button press go back to the management overview screen
     */
    public void goBackToManagementOverview() {
        noEventsSelectedLabel.textProperty().bind(translation.getStringBinding(""));
        mainCtrl.switchToManagementOverviewScreen();
    }
}
