package com.github.ruslanye.RankResolver.Model.Graphics;

import com.github.ruslanye.RankResolver.Model.Domain.Submit;
import com.github.ruslanye.RankResolver.Model.Utils.Config;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class LiveSubmit extends StackPane {

    private final Text status;
    private final Rectangle background;
    private final Submit submit;
    private final HBox box;
    private final Config conf;

    public LiveSubmit(Submit submit, Config conf) {
        this.submit = submit;
        this.conf = conf;
        box = new HBox();
        box.getChildren().addAll(new Text(submit.getContestant().getName()), new Text(" " + submit.getProblem().getId() + " "));
        status = new Text(submit.getStatus().getId());
        box.setAlignment(Pos.CENTER);
        box.getChildren().add(status);
        background = new Rectangle();
        background.setWidth(conf.liveSubmitWidth);
        background.setHeight(conf.liveSubmitHeight);
        background.setFill(Color.TRANSPARENT);
        background.setStroke(Color.BLACK);
        setPrefHeight(conf.liveSubmitHeight);
        setPrefWidth(conf.liveSubmitWidth);
        getChildren().addAll(background, box);
    }

    public void updateStatus() {
        FadeTransition fade = new FadeTransition(Duration.millis(conf.liveResultsFadeDuration), status);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(e -> {
            status.setText(submit.getStatus().getId());
            if (submit.getStatus().isOK())
                status.setFill(Color.GREEN);
            else
                status.setFill(Color.RED);
            status.setOpacity(1);
        });
        fade.play();
    }

    public Animation moveTo(double y) {
        return new Timeline(new KeyFrame(Duration.millis(conf.liveResultsMoveDuration), new KeyValue(layoutYProperty(), y)));
    }

    public Submit getSubmit() {
        return submit;
    }
}
