package com.github.ruslanye.RankResolver.Model.Graphics;

import com.github.ruslanye.RankResolver.Model.Domain.Status;
import com.github.ruslanye.RankResolver.Model.Domain.Submit;
import com.github.ruslanye.RankResolver.Model.Domain.SubmitObserver;
import com.github.ruslanye.RankResolver.Model.Utils.Config;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
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
    }

    private double getSubmitY(LiveSubmit sub) {
        int pos = submits.indexOf(sub);
        return getHeight() * MARGIN + sub.getPrefHeight() * pos;
    }

    private Animation delayExit(LiveSubmit submit, Duration duration) {
        Animation delay = new SequentialTransition(new PauseTransition(duration));
        Animation exit = submit.moveTo(-submit.getPrefHeight());
        delay.setOnFinished(e -> {
            if(!submits.contains(submit))
                return;
            submits.remove(submit);
            for (var sub : submits)
                sub.moveTo(getSubmitY(sub)).play();
            exit.setOnFinished((e1 -> {
                pane.getChildren().remove(submit);
            }));
            exit.play();
        });
        return delay;
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
                Animation pause = delayExit(toRemove, Duration.millis(conf.liveResultsStayDuration));
                pause.play();
            }
        });
        s.removeObserver(this);
    }

    @Override
    public void addSubmit(Submit s) {
        s.addObserver(this);
        Platform.runLater(() -> {
            if (submits.size() >= conf.liveResultsSubmitsLimit)
                return;
            LiveSubmit submit = new LiveSubmit(s, conf);
            submits.add(submit);
            submit.setLayoutX((getWidth() - submit.getPrefWidth()) / 2);
            submit.setLayoutY(getHeight());
            Animation enter = submit.moveTo(getSubmitY(submit));
            pane.getChildren().add(submit);
            enter.play();
            Animation timeout = delayExit(submit, Duration.millis(conf.liveResultsTimeout));
            timeout.play();
        });
    }
}
