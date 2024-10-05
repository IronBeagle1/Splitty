package client.scenes;

import client.utils.*;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import jakarta.persistence.EntityNotFoundException;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import static javafx.geometry.Pos.CENTER_RIGHT;

public class EventScreenCtrl implements Initializable, SimpleRefreshable{
    @FXML
    private TextField invitationCode;
    @FXML
    private Button addExpense;
    @FXML
    private Button allExpenses;
    @FXML
    private Button fromButton;
    @FXML
    private Button inButton;
    @FXML
    private Button settleDebtsButton;
    @FXML
    private Label eventNameLabel;
    @FXML
    private Label participantsLabel;
    @FXML
    private Label participantsName;
    @FXML
    private Label expenseLabel;
    @FXML
    private Button editParticipant;
    @FXML
    private Button addParticipant;
    @FXML
    private Button goBackButton;
    @FXML
    private ComboBox<String> cBoxParticipantExpenses;
    @FXML
    private Label errorInvalidParticipant;
    @FXML
    private ListView<HBox> expensesLogListView;
    @FXML
    private ComboBox<Locale> languageIndicator;
    @FXML
    private Button addTagButton;
    @FXML
    private Button showStatisticsButton;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final Translation translation;
    private final LanguageIndicatorCtrl languageCtrl;
    private final ImageUtils imageUtils;
    private final StringGenerationUtils stringUtils;
    private Event event;
    private final Map<Long, HBox> hBoxMap;
    private Button selectedExpenseListButton;
    private final EmailHandler emailHandler;
    @FXML
    private Button testEmailButton;
    @FXML
    private Label emailFeedbackLabel;
    @FXML
    private Button emailInviteButton;
    @FXML
    private Button transferMoneyButton;
    private final Styling styling;
    /**
     * Constructor
     *
     * @param server      the ServerUtils instance
     * @param mainCtrl    the MainCtrl instance
     * @param translation the Translation to use
     * @param languageCtrl the LanguageIndicator to use
     * @param imageUtils  the ImageUtils to use
     * @param styling     the Styling to use
     * @param stringUtils the StringGenerationUtils to use
     * @param emailHandler Handles email related functionality
     */
    @Inject
    public EventScreenCtrl(ServerUtils server, MainCtrl mainCtrl, Translation translation,
                           LanguageIndicatorCtrl languageCtrl, ImageUtils imageUtils,
                StringGenerationUtils stringUtils, Styling styling, EmailHandler emailHandler) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.translation = translation;
        this.imageUtils = imageUtils;
        this.stringUtils = stringUtils;
        this.event = null;
        this.hBoxMap = new HashMap<>();
        this.languageCtrl = languageCtrl;
        this.selectedExpenseListButton = null;
        this.styling = styling;
        this.emailHandler = emailHandler;
    }

    /**
     * Initialize basic features of the application, bind text to be translated,
     * set images for edit, add participants  and go back buttons
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
        invitationCode.setEditable(false);
        testEmailButton.textProperty().bind(translation.getStringBinding("Event.Button.TestEmail"));
        participantsName.textProperty()
            .bind(translation.getStringBinding("Participants.DisplayName.EventScreen"));
        expenseLabel.textProperty()
            .bind(translation.getStringBinding("Expense.Label.Display.EventScreen"));
        addExpense.textProperty()
            .bind(translation.getStringBinding("Event.Button.AddExpense"));
        errorInvalidParticipant.textProperty()
            .bind(translation.getStringBinding("empty"));
        settleDebtsButton.textProperty()
            .bind(translation.getStringBinding("Event.Button.SettleDebts"));
        addTagButton.textProperty()
            .bind(translation.getStringBinding("Event.Button.AddTag"));
        showStatisticsButton.textProperty()
            .bind(translation.getStringBinding("Event.Button.Statistics"));
        initializeEditTitle();
        addGeneratedImages();
        initializeParticipantsCBox();
        if (emailHandler.isConfigured()) {
            enableEmailFeatures();

        }
        emailFeedbackLabel.textProperty().bind(translation.getStringBinding("empty"));
        emailInviteButton.textProperty().bind(translation.getStringBinding("Event.Button.InviteByEmail"));
        languageCtrl.initializeLanguageIndicator(languageIndicator);
        transferMoneyButton.textProperty().bind(translation.getStringBinding("Event.Button.TransferMoney"));
        setButtonsNames("...");
    }

    /**
     * Enables the email features
     */
    private void enableEmailFeatures() {
        emailInviteButton.setOnAction(a -> switchToInviteEmail());
        styling.removeStyling(testEmailButton, "disabledButton");
        styling.removeStyling(emailInviteButton, "disabledButton");
        testEmailButton.setOnAction(event -> sendTestEmail());
    }
    /**
     * Switches to the invite email screen
     */
    public void switchToInviteEmail() {
        if (emailHandler.isConfigured()){
            mainCtrl.switchScreens(EmailInviteCtrl.class);
        }
    }

    /**
     * Adds generated images to the buttons
     */
    private void addGeneratedImages() {
        ImageView editParticipantImage = imageUtils.generateImageView("editing.png", 15);
        editParticipant.setGraphic(editParticipantImage);
        ImageView addParticipantImage = imageUtils.generateImageView("add-participant.png", 15);
        addParticipant.setGraphic(addParticipantImage);
        ImageView goBackImage = imageUtils.generateImageView("goBack.png", 15);
        goBackButton.setGraphic(goBackImage);
    }

    /**
     * Initializes the Combobox so the participant chosen is
     * reflected in the filter buttons
     */
    public void initializeParticipantsCBox() {
        cBoxParticipantExpenses.valueProperty()
            .addListener((observable, oldValue, newValue) -> {
                boolean validParticipant = false;
                Set<Participant> participants = event.getParticipants();
                for(Participant participant: participants)
                    if(participant.getName().equalsIgnoreCase(newValue)){
                        validParticipant = true;
                        break;
                    }
                if(validParticipant) {
                    errorInvalidParticipant.textProperty()
                        .bind(translation.getStringBinding("empty"));
                    setButtonsNames(newValue);
                }
                else {
                    if(newValue == null || newValue.isEmpty())
                        setButtonsNames("...");
                    else {
                        errorInvalidParticipant.textProperty()
                            .bind(translation.getStringBinding(
                                "Event.Label.Error.InvalidParticipant"));
                    }
                }
            });
    }

    private void initializeEditTitle() {
        eventNameLabel.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getClickCount() == 2)
                mainCtrl.switchScreens(EditTitleCtrl.class);
        });
    }

    /***
     * Updates all data viewed
     * @param event the Event to extract data from
     */
    public void refresh(Event event){
        this.event = event;
        updateEventText();
        participantsLabel.setText(generateParticipantString(event));
        updateParticipantsDropdown(event, cBoxParticipantExpenses.getItems());
        refreshExpenseList();
        errorInvalidParticipant.textProperty()
                .bind(translation.getStringBinding("empty"));
        //buttonsHBox.getChildren().clear();
        languageCtrl.refresh(languageIndicator);
    }

    /**
     * Open the title editing screen.
     */
    public void editTitle() {
        mainCtrl.switchScreens(EditTitleCtrl.class);
    }

    /**
     * UI for editing current participants that needs to be implemented when the button is pressed
     */
    public void editCurrentParticipants(){
        mainCtrl.switchScreens(ParticipantListScreenCtrl.class);
    }

    /**
     * UI for adding a participant that needs to be implemented when the button is pressed
     */
    public void addParticipants(){
        mainCtrl.switchScreens(ParticipantScreenCtrl.class);
    }

    /**
     * UI for adding an expense that needs to be implemented when the button is pressed
     */
    public void addExpense(){
        mainCtrl.switchScreens(ExpenseScreenCtrl.class);
    }

    /**
     * set the name of the event in the event screen
     */
    private void updateEventText() {
        eventNameLabel.setText(event.getTitle());
        invitationCode.setText(event.getId());
    }

    /**
     * Generates a String listing current event participants
     * @param event the Event data to use
     * @return a String listing current event participants
     */
    public String generateParticipantString(Event event){
        StringBuilder participantsText = new StringBuilder();
        Iterator<Participant> participantIterator = event.getParticipants().iterator();
        while(participantIterator.hasNext()){
            Participant current = participantIterator.next();
            if(participantIterator.hasNext()) participantsText.
                    append(current.getName()).append(", ");
            else participantsText.append(current.getName());
        }
        if(participantsText.toString().isEmpty()) {
            return("There are no participants");
        }
        else return participantsText.toString();
    }

    /**
     * show the participants of the current event
     * @param event the Event data to use
     * @param boxItems the ObservableList to load data into/from
     */
    public void updateParticipantsDropdown(Event event, ObservableList<String> boxItems){
        List<String> names = new ArrayList<>();
        for (Participant current : event.getParticipants()) {
            names.add(current.getName());
        }
        boxItems.retainAll(names);
        names.removeAll(boxItems);
        boxItems.addAll(names);
    }

    /**
     * In the context of this app, when choosing one of the participants
     * we want to see 3 buttons, 2 of which are related to the name of the participant.
     * This method generates those buttons and adds them to the hBox
     * @param name the name from the buttons
     */
    public void setButtonsNames(String name) {
        allExpenses.textProperty().bind(translation
            .getStringBinding("Event.Button.ShowAllExpenses"));
        fromButton.textProperty().bind(createFilterString(name, "From"));
        inButton.textProperty().bind(createFilterString(name, "Including"));
    }

    /**
     * Creates the strings for the filer buttons above the expense overview taking dynamic translations into account.
     * If no participant has been selected in the event overview, then "..." will be shown.
     * Else the participant name will be shown.
     * @param name Name of the selected participant
     * @param filter String denoting the filter type: from or to
     * @return Observable string for the filter button text
     */
    public ObservableValue<String> createFilterString(String name, String filter) {
        String show;
        if(name == null || name.isEmpty() || name.equals("..."))
            show = "...";
        else
            show = name;
        Map<String, String> subValues = new HashMap<>();
        subValues.put("participant", show);
        return Bindings.concat(translation.getStringSubstitutionBinding("Event.String." + filter,
            subValues));
    }

    /**
     * the action when we press the "All" button
     */
    public void showAllExpenses(){
        this.selectedExpenseListButton = allExpenses;
        refreshExpenseList();
    }

    /***
     * the action when we press the "From ..." button
     */
    public void fromFilter(){
        this.selectedExpenseListButton = fromButton;
        refreshExpenseList();
    }

    /***
     * the action when we press the "Including ..." button
     */
    public void includingFilter(){
        this.selectedExpenseListButton = inButton;
        refreshExpenseList();
    }

    private void refreshExpenseList() {
        Button selectedButton = selectedExpenseListButton;
        if(selectedButton == null) selectedButton = allExpenses;
        if(selectedButton == allExpenses) showAllExpenseList(event, expensesLogListView);
        else if(selectedButton == fromButton) fromFilter(event, expensesLogListView, cBoxParticipantExpenses.getValue());
        else if(selectedButton == inButton) includingFilter(event, expensesLogListView, cBoxParticipantExpenses.getValue());
    }

    /**
     * Generates a list of hBoxes stating with a human-readable String
     * that presents a quick overview of the expense presented
     * @param event the Event data to use
     * @param expensesLogListView the ListView containing expense data HBoxes
     */
    public void showAllExpenseList(Event event, ListView<HBox> expensesLogListView) {
        expensesLogListView.getItems().clear();
        //We only want to load this in once! IO is expensive.
        Image removeImage = imageUtils.loadImageFile("x_remove.png");
        for(Expense expense: event.getExpenses()) {
            if(!expense.getParticipantsInExpense().isEmpty()){
                HBox expenseBox = generateExpenseBox(expense, event, removeImage, expensesLogListView.getPrefWidth());
                expensesLogListView.getItems().add(expenseBox);
            }
        }
    }

    /**
     *
     * @param expense the expense for which we need to generate the HBox
     * @param event the Event data to use
     * @param removeImage the Image to use for the X icon
     * @param width the width of the generated hBox
     * @return the generated hBox, containing the expense details
     */
    public HBox generateExpenseBox(Expense expense, Event event, Image removeImage, double width) {
        ObservableValue<String> expenseTextValue;
        if(expense.getPriceInCents() > 0)
            expenseTextValue = stringUtils.generateTextForExpenseLabel(expense, event.getParticipants().size());
        else
            expenseTextValue = stringUtils.generateTextForMoneyTransfer(expense);
        Label expenseText = generateExpenseLabel(expense.getId(), expenseTextValue);
        ImageView xButton = generateRemoveButton(expense.getId(), removeImage);
        Label dateLabel = new Label();
        dateLabel.setPrefWidth(45);
        dateLabel.setWrapText(true);
        expenseText.setPrefWidth(200);
        Calendar expenseCalendar  = Calendar.getInstance();
        expenseCalendar.setTime(expense.getDate());
        Calendar now = Calendar.getInstance();
        SimpleDateFormat shortDate = new SimpleDateFormat("dd/MM");
        SimpleDateFormat fullDate = new SimpleDateFormat("dd/MM/yyyy");
        if(expenseCalendar.get(Calendar.YEAR) != now.get(Calendar.YEAR))
            dateLabel.setText(fullDate.format(expense.getDate()));
        else
            dateLabel.setText(shortDate.format(expense.getDate()));
        HBox xHBox = new HBox(xButton);
        HBox.setHgrow(xHBox, javafx.scene.layout.Priority.ALWAYS);
        xHBox.setAlignment((CENTER_RIGHT));
        Tag item = expense.getExpenseTag();
        Label tagLabel = new Label(item.getTagName());
        tagLabel.setWrapText(true);
        tagLabel.setAlignment(Pos.CENTER);
        tagLabel.setStyle("-fx-background-color: " + item.getColorCode() + ";" +
            "-fx-background-radius: 3;" +
            "-fx-padding: 1 2 1 2;" +
            "-fx-text-fill: white;");
        HBox datehBox = new HBox(dateLabel);
        datehBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(datehBox, javafx.scene.layout.Priority.ALWAYS);
        HBox justExpenseBox = new HBox(datehBox, expenseText, tagLabel);
        justExpenseBox.setOnMouseEntered(mouseEvent -> mainCtrl.getEventScene().setCursor(Cursor.HAND));
        justExpenseBox.setOnMouseExited(mouseEvent -> mainCtrl.getEventScene().setCursor(Cursor.DEFAULT));
        justExpenseBox.setOnMouseClicked(mouseEvent -> mainCtrl.switchToEditExpense(expense.getId()));
        justExpenseBox.setAlignment(Pos.CENTER);
        HBox expenseBox = new HBox(justExpenseBox, xHBox);
        expenseBox.setPrefWidth(width);
        expenseBox.setSpacing(10);
        hBoxMap.put(expense.getId(), expenseBox);
        expenseBox.setPrefWidth(width);
        expenseBox.setAlignment(Pos.CENTER_LEFT);
        return expenseBox;
    }

    /**
     * Creates a label that will later be added to the hBox from the expense listview
     * Furthermore, it allows the client to access set expense and edit it
     * @param expenseId the id of the expense we are creating a label for
     * @param expenseDescription the description of the expense
     * @return the created label
     */
    public Label generateExpenseLabel(long expenseId, ObservableValue<String> expenseDescription) {
        Label expense = new Label();
        expense.textProperty().bind(expenseDescription);
        expense.setWrapText(true);
        expense.setOnMouseEntered(mouseEvent -> mainCtrl.getEventScene().setCursor(Cursor.HAND));
        expense.setOnMouseExited(mouseEvent -> mainCtrl.getEventScene().setCursor(Cursor.DEFAULT));
        expense.setOnMouseClicked(mouseEvent -> mainCtrl.switchToEditExpense(expenseId));
        return expense;
    }

    /**
     * Generates a remove "button" for the expenses from the listview
     * Furthermore, it allows the client to remove set event by pressing the 'X'
     * @param expenseId the id of the expense to be generated
     * @param image the Image to be placed on the button
     * @return the generated button
     * this exception is thrown
     */
    public ImageView generateRemoveButton (long expenseId, Image image) {
        ImageView imageView = imageUtils.generateImageView(image, 15);
        imageView.setPickOnBounds(true);
        imageView.setOnMouseEntered(mouseEvent -> mainCtrl.getEventScene().setCursor(Cursor.HAND));
        imageView.setOnMouseExited(mouseEvent -> mainCtrl.getEventScene().setCursor(Cursor.DEFAULT));
        imageView.setOnMouseClicked(mouseEvent -> removeFromList(expenseId));
        return imageView;
    }

    /**
     * Deletes an expense from the server. It also reflects it in the client
     * by deleting the set expense from the listview
     * @param expenseId the id of the expense we want to delete
     */
    public void removeFromList(long expenseId){
        server.deleteExpenseForEvent(event.getId(), expenseId);
        HBox hBox = hBoxMap.get(expenseId);
        hBoxMap.remove(expenseId);
        expensesLogListView.getItems().remove(hBox);
    }


    /**
     * UI for settling current debts
     */
    public void settleDebts() {
        mainCtrl.switchScreens(SettleDebtsScreenCtrl.class);
    }

    /**
     * go back to the main screen
     */
    public void switchToMainScreen() {
        mainCtrl.showMainScreen();
    }

    /**
     * Sends a test email to the user
     */
    public void sendTestEmail() {
        if (!emailHandler.isConfigured()){
            return;
        }
        styling.changeStyling(emailFeedbackLabel, "errorText", "successText");
        emailFeedbackLabel.textProperty()
                .bind(translation.getStringBinding("Event.Label.EmailFeedback.Sending"));
        Thread t = new Thread(() ->
        {
            boolean emailSent = emailHandler.sendTestEmail();
            Platform.runLater(() -> {
                if (emailSent) {
                    styling.changeStyling(emailFeedbackLabel, "errorText", "successText");
                    emailFeedbackLabel.textProperty()
                            .bind(translation.getStringBinding("Event.Label.EmailFeedback.Success"));
                } else if (emailHandler.isConfigured()) {
                    styling.changeStyling(emailFeedbackLabel, "successText", "errorText");
                    emailFeedbackLabel.textProperty()
                            .bind(translation.getStringBinding("Event.Label.EmailFeedback.Fail"));
                }
            });
        });
        t.start();
    }
    /**
     * Filters the expenses, showing only the ones that were paid by a certain participant
     * @param event the Event data to use
     * @param expensesLogListView the ListView containing expense data HBoxes
     * @param name the participant name to filter by
     */
    public void fromFilter(Event event, ListView<HBox> expensesLogListView, String name) {
        expensesLogListView.getItems().clear();
        Image removeImage = imageUtils.loadImageFile("x_remove.png");
        for(Expense expense: event.getExpenses())
            if(expense.getOwedTo().getName().equals(name)) {
                HBox expenseBox = generateExpenseBox(expense, event, removeImage, expensesLogListView.getPrefWidth());
                expensesLogListView.getItems().add(expenseBox);
            }
    }

    /**
     * Filters the expenses, showing the one a certain participant is part of
     * @param event the Event data to use
     * @param expensesLogListView the ListView containing expense data HBoxes
     * @param name the participant name to filter by
     */
    public void includingFilter(Event event, ListView<HBox> expensesLogListView, String name) {
        expensesLogListView.getItems().clear();
        Set<Participant> eventParticipants = event.getParticipants();
        Participant selectedParticipant = null;
        for(Participant participant: eventParticipants) {
            if(participant.getName().equals(name)) {
                selectedParticipant = participant;
                break;
            }
        }
        if(selectedParticipant == null)
            throw new EntityNotFoundException("The participant doesn't exist");
        Set<Expense> eventExpenses = event.getExpenses();
        Image removeImage = imageUtils.loadImageFile("x_remove.png");
        for(Expense expense: eventExpenses) {
            Set<Participant>participantsInExpense = expense.getParticipantsInExpense();
            if(participantsInExpense.contains(selectedParticipant)) {
                HBox expenseBox = generateExpenseBox(expense, event, removeImage, expensesLogListView.getPrefWidth());
                expensesLogListView.getItems().add(expenseBox);
            }
        }
    }

    /**
     * Transfer to money transfer screen.
     */
    public void transferMoney() {
        mainCtrl.switchScreens(TransferMoneyCtrl.class);
    }

    /**
     * when pressing the add Tag button it switches to that screen
     */
    public void switchToAddTag() {
        mainCtrl.switchScreens(AddTagCtrl.class);
    }

    /**
     * when pressing on the Show statistics button it switches to the statistics screen
     */
    public void switchToStatistics() {
        mainCtrl.switchScreens(StatisticsScreenCtrl.class);
    }
}
