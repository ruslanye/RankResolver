package com.github.ruslanye.RankResolver.Controller;

import com.github.ruslanye.RankResolver.Model.Utils.Config;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Optional;
import java.util.Properties;

public class SettingsController {

    @FXML
    public TextField startTimeField;
    @FXML
    public TextField durationField;
    @FXML
    public TextField locationField;
    private boolean changed = false;

    public void reloadConfig() {
        Properties prop = Config.loadProperties();
        startTimeField.setText(prop.getProperty("startTime"));
        durationField.setText(prop.getProperty("duration"));
        locationField.setText(prop.getProperty("location"));
        changed = false;
    }

    public void saveChanges() {
        if (!changed)
            return;
        Properties prop = new Properties();
        prop.setProperty("startTime", startTimeField.getText());
        prop.setProperty("location", locationField.getText());
        prop.setProperty("duration", durationField.getText());
        if (!Config.validate(prop)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Something is wrong");
            alert.setContentText("Some properties are invalid");
        }
        Config.saveProperties(prop);
        changed = false;
    }

    @FXML
    public void saveChangesAction() {
        saveChanges();
    }

    @FXML
    public void discardChangesAction() {
        reloadConfig();
    }

    public void textChanged(ObservableValue<? extends String> observable,
                            String oldValue, String newValue) {
        changed = true;
    }

    @FXML
    public void chooseFileAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose submits location");
        File file = fileChooser.showOpenDialog(((Button) event.getSource()).getScene().getWindow());
        if (file != null)
            locationField.setText(file.getAbsolutePath());
    }

    @FXML
    public boolean onClose() {
        if (changed) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                            "You have unsaved changes. Are you sure you want to exit?",
                            ButtonType.YES,
                            ButtonType.NO);
            alert.setTitle("Unsaved changes");
            Optional<ButtonType> result = alert.showAndWait();

            return result.get() == ButtonType.NO;
        }
        return false;
    }

    @FXML
    public void initialize(){
        startTimeField.setPromptText(Config.DATE_FORMAT);
        startTimeField.textProperty().addListener(this::textChanged);
        durationField.textProperty().addListener(this::textChanged);
        locationField.textProperty().addListener(this::textChanged);
        reloadConfig();
    }
}
