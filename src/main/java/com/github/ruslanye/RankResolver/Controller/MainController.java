package com.github.ruslanye.RankResolver.Controller;

import com.github.ruslanye.RankResolver.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
//    @FXML
//    public void showSettings(ActionEvent event) {
//        Stage stage = new Stage();
//        Parent root;
//        FXMLLoader loader = new FXMLLoader(App.class.getResource("View/Settings.fxml"));
//        try {
//            root = loader.load();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return;
//        }
//        stage.setScene(new Scene(root));
//        stage.setTitle("Setup contest");
//        stage.initModality(Modality.APPLICATION_MODAL);
//        SettingsController controller = loader.getController();
//        stage.setOnCloseRequest(e -> {
//            if (controller.onClose())
//                e.consume();
//        });
//        stage.show();
//    }

    @FXML
    public void startContest(ActionEvent event){
        Parent root;
        FXMLLoader loader = new FXMLLoader(App.class.getResource("View/Contest.fxml"));
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Node node = (Node) event.getSource();
        Scene scene = node.getScene();
        Stage stage = (Stage) scene.getWindow();
        scene.setRoot(root);
        stage.setTitle("Contest");
        ContestController controller = loader.getController();
        controller.startContest();
    }
}
