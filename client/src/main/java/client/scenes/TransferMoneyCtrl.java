package client.scenes;

import client.utils.ServerUtils;
import client.utils.TransferMoneyUtils;
import client.utils.Translation;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class TransferMoneyCtrl implements Initializable, SimpleRefreshable {
    @FXML
    private Button transferButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label header;
    @FXML
    private Label toLabel;
    @FXML
    private Label fromLabel;
    @FXML
    private Label amountLabel;
    @FXML
    private ChoiceBox<String> currencyChoiceBox = new ChoiceBox<>();
    @FXML
    private TextField amountTextField;
    @FXML
    private ComboBox<Participant> fromComboBox;
    @FXML
    private ComboBox<Participant> toComboBox;
    @FXML
    private Label amountError;
    @FXML
    private Label fromError;
    @FXML
    private Label toError;
    private final StringProperty sameParticipants = new SimpleStringProperty();
    private final ServerUtils serverUtils;
    private final Translation translation;
    private final MainCtrl ctrl;
    private final TransferMoneyUtils utils;

    /**
     * Controller for money transfer screen
     * @param translation Translation utilities
     * @param serverUtils Utilities for server communication
     * @param ctrl Main controller
     * @param utils Utilities for transfering money
     */
    @Inject
    public TransferMoneyCtrl(Translation translation, ServerUtils serverUtils, MainCtrl ctrl,
                             TransferMoneyUtils utils) {
        this.translation = translation;
        this.serverUtils = serverUtils;
        this.ctrl = ctrl;
        this.utils = utils;
    }

    /**
     * Initialize the FXML fields.
     * @param url URL of something
     * @param resources Resources used
     */
    @Override
    public void initialize(URL url, ResourceBundle resources) {
        toLabel.textProperty().bind(translation.getStringBinding("TransferMoney.Label.To"));
        fromLabel.textProperty().bind(translation.getStringBinding("TransferMoney.Label.From"));
        header.textProperty().bind(translation.getStringBinding("TransferMoney.title"));
        cancelButton.textProperty().bind(translation.getStringBinding("TransferMoney.Button.Cancel"));
        transferButton.textProperty().bind(translation.getStringBinding("TransferMoney.Button.Confirm"));
        amountLabel.textProperty().bind(translation.getStringBinding("TransferMoney.Label.Amount"));
        amountError.textProperty().bind(translation.getStringBinding("TransferMoney.Label.AmountError"));
        sameParticipants.bind(translation.getStringBinding("TransferMoney.Error.SameParticipants"));
        fromError.textProperty().bind(translation.getStringBinding("TransferMoney.Label.ParticipantError"));
        toError.textProperty().bind(translation.getStringBinding("TransferMoney.Label.ParticipantError"));

        fromComboBox.setItems(utils.getParticipants());
        toComboBox.setItems(utils.getParticipants());

        currencyChoiceBox.setItems(utils.getCurrencies());
        utils.setSelectedCurrency(currencyChoiceBox.valueProperty());

        utils.setIsAmountErrorVisible(amountError.visibleProperty());
        utils.setAmount(amountTextField.textProperty());

        utils.setFrom(fromComboBox.valueProperty());
        utils.setIsFromErrorVisible(fromError.visibleProperty());
        utils.setFromAction(fromComboBox.onActionProperty());

        utils.setTo(toComboBox.valueProperty());
        utils.setIsToErrorVisible(toError.visibleProperty());
        utils.setToAction(toComboBox.onActionProperty());

        Callback<ListView<Participant>, ListCell<Participant>> cellFactory = listView -> new ListCell<>() {
            @Override
            protected void updateItem(Participant participant, boolean empty) {
                super.updateItem(participant, empty);
                if(participant == null || empty)
                    setText(null);
                else
                    setText(participant.getName());
            }
        };
        fromComboBox.setCellFactory(cellFactory);
        fromComboBox.setButtonCell(cellFactory.call(null));
        toComboBox.setCellFactory(cellFactory);
        toComboBox.setButtonCell(cellFactory.call(null));
    }

    /**
     * Switch screens when clicking cancel button
     */
    public void cancel() {
        ctrl.switchScreens(EventScreenCtrl.class);
    }

    /**
     * Switch screens when clicking confirm button.
     */
    public void confirm() {
        if(utils.hasErrors())
            return;
        if(utils.hasInvalidParticipants()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, sameParticipants.getValue());
            alert.showAndWait();
        } else {
            utils.send();
            ctrl.switchScreens(EventScreenCtrl.class);
        }
    }

    /**
     * Update names of changed participants
     * @param event the new Event data to process
     */
    @Override
    public void refresh(Event event) {
        utils.refresh(event);
        fromComboBox.requestFocus();
    }
}
