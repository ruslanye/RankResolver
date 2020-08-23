package com.github.ruslanye.RankResolver.Model.Graphics;

import com.github.ruslanye.RankResolver.Model.Domain.Submit;
import com.github.ruslanye.RankResolver.Model.Utils.Config;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class LiveSubmit extends StackPane {
    private final Rectangle background;
    private final Submit submit;
    private final HBox box;
    private final Config conf;
    private final TextBox name;
    private final TextBox problem;
    private final TextBox time;
    private final TextBox status;
    private double width;
    private double height;

    public LiveSubmit(Submit submit, Config conf, double width, double height) {
        this.submit = submit;
        this.conf = conf;
        this.width = width;
        this.height = height;

        box = new HBox();

        name = new TextBox(submit.getContestant().getName());
        problem = new TextBox(submit.getProblem().getId());
        time = new TextBox(submit.getTime().toString());
        status = new TextBox(submit.getStatus().getId());

        box.getChildren().addAll(name, problem, time, status);
        box.setPadding(new Insets(0 ,10, 0, 10));

        status.setAlignment(Pos.CENTER);
        box.setAlignment(Pos.CENTER_LEFT);

        background = new Rectangle();
        background.setFill(Color.TRANSPARENT);
        background.setStroke(Color.BLACK);

        getChildren().addAll(background, box);

        updateWidth(width);
        updateHeight(height);
    }

    public void updateStatus() {
        FadeTransition fade = new FadeTransition(Duration.millis(conf.liveResultsFadeDuration), status.getText());
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(e -> {
            status.getText().setText(submit.getStatus().getId());
            if (submit.getStatus().isOK())
                status.setFill(conf.solvedColor);
            else
                status.setFill(conf.failedColor);
            status.getText().setOpacity(1);
        });
        fade.play();
    }

    public Animation moveTo(double y) {
        return new Timeline(new KeyFrame(Duration.millis(conf.liveResultsMoveDuration), new KeyValue(layoutYProperty(), y)));
    }

    public Submit getSubmit() {
        return submit;
    }

    public void updateWidth(double newWidth) {
        double boxWidth = conf.boxWidth * newWidth / width;
        width = newWidth;
        background.setWidth(width);
        name.updateWidth(width - 6 * boxWidth);
        problem.updateWidth(boxWidth);
        time.updateWidth(3 * boxWidth);
        status.updateWidth(2 * boxWidth);
        setPrefWidth(width);
    }

    public void updateHeight(double newHeight) {
        height = newHeight;
        background.setHeight(height);
        status.updateHeight(height);
        setPrefHeight(height);
    }

    public void setFill(Paint p) {
        background.setFill(p);
    }
}
