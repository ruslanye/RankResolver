package com.github.ruslanye.RankResolver.Model.Graphics;

import com.github.ruslanye.RankResolver.Model.Domain.Status;
import com.github.ruslanye.RankResolver.Model.Domain.Submit;
import com.github.ruslanye.RankResolver.Model.Domain.SubmitObserver;
import com.github.ruslanye.RankResolver.Model.Utils.Config;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.List;

public class LiveResults extends Stage implements SubmitObserver {

    private static final Double MARGIN = 0.05;
    private final Config conf;
    private final List<LiveSubmit> submits;
    private final List<Animation> animations;
    private final Pane pane;

    public LiveResults(Config conf) {
        this.conf = conf;
        setTitle("LiveResults");
        pane = new Pane();
        Scene scene = new Scene(pane);
        setScene(scene);
        setWidth(conf.liveResultsWidth);
        setHeight(conf.liveResultsHeight);
        submits = new LinkedList<>();
        animations = new LinkedList<>();
    }

    private void move(LiveSubmit submit, double y) {
        Timeline t = new Timeline(new KeyFrame(Duration.millis(1000), new KeyValue(submit.layoutYProperty(), y)));
        t.play();
    }

    private double getSubmitY(LiveSubmit sub) {
        int pos = submits.indexOf(sub);
        return getHeight() * MARGIN + sub.getPrefHeight() * pos;
    }

    @Override
    public void notify(Submit s, Status oldStatus) {
        Platform.runLater(() -> {
            LiveSubmit toRemove = null;
            for (var sub : submits)
                if (sub.getSubmit() == s) {
                    toRemove = sub;
                    break;
                }
            if (toRemove != null) {
                toRemove.updateStatus();
                Animation a = new SequentialTransition(new PauseTransition(Duration.seconds(2)));
                Animation exit = new Timeline((new KeyFrame(Duration.seconds(1), new KeyValue(toRemove.layoutYProperty(), -toRemove.getPrefHeight()))));
                LiveSubmit finalToRemove = toRemove;
                a.setOnFinished((e -> {
                    submits.remove(finalToRemove);
                    for (var sub : submits)
                        move(sub, getSubmitY(sub));
                    exit.setOnFinished((e1 -> {
                        pane.getChildren().remove(finalToRemove);
                    }));
                    exit.play();
                }));
                a.play();
            }
        });
        s.removeObserver(this);
    }

    @Override
    public void addSubmit(Submit s) {
        s.addObserver(this);
        Platform.runLater(() -> {
            LiveSubmit submit = new LiveSubmit(s, conf.submitWidth, conf.submitHeight);
            submits.add(submit);
            submit.setLayoutX((getWidth() - submit.getPrefWidth()) / 2);
//            submit.setLayoutY(getSubmitY(submit));
            submit.setLayoutY(getHeight());
            Animation enter = new Timeline((new KeyFrame(Duration.seconds(1), new KeyValue(submit.layoutYProperty(), getSubmitY(submit)))));
            pane.getChildren().add(submit);
            enter.play();
        });
    }
}
