package com.github.ruslanye.RankResolver.Controller;

import com.github.ruslanye.RankResolver.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    public void showSettings() {
        Stage stage = new Stage();
        Parent root;
        try {
            root = FXMLLoader.load(App.class.getResource("View/Settings.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        stage.setScene(new Scene(root));
        stage.setTitle("Setup contest");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    public void settingsButtonClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY)
            showSettings();
    }

    public void settingsButtonReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            showSettings();
    }
}
