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
    private final List<LiveSubmit> liveSubs;
    private final Pane pane;
    private int count;

    public LiveResults(Config conf) {
        this.conf = conf;
        count = 0;

        setTitle("LiveResults");
        pane = new Pane();

        Scene scene = new Scene(pane);
        setScene(scene);
        setWidth(conf.liveResultsWidth);
        setHeight(conf.liveResultsHeight);
        liveSubs = new LinkedList<>();
    }

    private double getLimit(){
        return conf.liveResultsSubmitsLimit;
    }

    private double getSubmitX(){
        return getWidth() * MARGIN;
    }

    private double getSubmitY(LiveSubmit sub) {
        int pos = liveSubs.indexOf(sub);
        return getHeight() * MARGIN + sub.getMinHeight() * pos;
    }

    private double getSubmitWidth(){
        return getWidth() * (1 - 2 * MARGIN);
    }

    private double getSubmitHeight(){
        return getHeight() * (1 - 2 * MARGIN) / getLimit();
    }

    private Animation delayExit(LiveSubmit submit, Duration duration) {
        Animation delay = new SequentialTransition(new PauseTransition(duration));
        Animation exit = submit.moveTo(-submit.getPrefHeight());
        delay.setOnFinished(e -> {
            if(!liveSubs.contains(submit))
                return;
            liveSubs.remove(submit);
            for (var sub : liveSubs)
                sub.moveTo(getSubmitY(sub)).play();
            exit.setOnFinished((e1 -> {
                pane.getChildren().remove(submit);
            }));
            exit.play();
        });
        return delay;
    }

    @Override
    public void notify(Submit sub, Status oldStatus) {
        Platform.runLater(() -> {
            synchronized (this){
                LiveSubmit toRemove = null;
                for (var liveSub : liveSubs)
                    if (liveSub.getSubmit() == sub) {
                        toRemove = liveSub;
                        break;
                    }
                if (toRemove != null) {
                    toRemove.updateStatus();
                    Animation pause = delayExit(toRemove, conf.liveResultsStayDuration);
                    pause.play();
                }
                sub.removeObserver(this);
            }
        });
    }

    @Override
    public void addSubmit(Submit sub) {
        Platform.runLater(() -> {
            synchronized (this){
                sub.addObserver(this);
                if (liveSubs.size() >= conf.liveResultsSubmitsLimit)
                    return;
                LiveSubmit liveSub = new LiveSubmit(sub, conf, getSubmitWidth(), getSubmitHeight());
                if(count++%2==0)
                    liveSub.setFill(conf.rowColor1);
                else
                    liveSub.setFill(conf.rowColor2);
                liveSubs.add(liveSub);
                liveSub.setLayoutX(getSubmitX());
                liveSub.setLayoutY(getHeight());
                Animation enter = liveSub.moveTo(getSubmitY(liveSub));
                pane.getChildren().add(liveSub);
                enter.play();
                Animation timeout = delayExit(liveSub, conf.liveResultsTimeout);
                timeout.play();
            }
        });
    }
}
