package com.github.ruslanye.RankResolver.Model.Graphics;

import com.github.ruslanye.RankResolver.Model.Domain.Contest;
import com.github.ruslanye.RankResolver.Model.Domain.Contestant;
import com.github.ruslanye.RankResolver.Model.Domain.ContestantObserver;
import com.github.ruslanye.RankResolver.Model.Domain.Submit;
import com.github.ruslanye.RankResolver.Model.Utils.Config;
import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.*;

public class LiveRanking extends Stage implements ContestantObserver {
    private static final Double MARGIN = 0.05;
    protected final Contest contest;
    protected final Config conf;
    protected final Pane pane;
    protected final List<LiveContestant> ranking;
    protected final Map<Contestant, LiveContestant> liveCons;
    protected final Queue<Animation> animations;

    public LiveRanking(Contest contest, Config conf) {
        this.contest = contest;
        this.conf = conf;
        animations = new LinkedList<>();
        setTitle("LiveRanking");
        pane = new Pane();
        Scene scene = new Scene(pane);
        setScene(scene);
        setWidth(conf.liveResultsWidth);
        setHeight(conf.liveResultsHeight);
        liveCons = new HashMap<>();
        ranking = new ArrayList<>();
        for (var con : contest.getContestants()) {
            LiveContestant liveCon = new LiveContestant(con, contest, conf);
            ranking.add(liveCon);
            liveCons.put(con, liveCon);
            liveCon.setLayoutX((getWidth() - liveCon.getPrefWidth()) / 2);
            liveCon.setLayoutY(getContestantY(liveCon));
            pane.getChildren().add(liveCon);
        }
    }

    protected double getContestantY(LiveContestant con) {
        int pos = ranking.indexOf(con);
        return getHeight() * MARGIN + con.getPrefHeight() * pos;
    }

    protected void reRank() {
        ranking.sort((x, y) -> {
            var con1 = x.getContestant();
            var con2 = y.getContestant();
            var scoreComp = Long.compare(con2.getScore(), con1.getScore());
            var timeComp = Long.compare(con1.getTotalTime(),
                    con2.getTotalTime());
            return scoreComp == 0 ? timeComp == 0 ? con1.getName().compareTo(con2.getName()) : timeComp : scoreComp;
        });
    }

    protected synchronized void onMoveFinished(ActionEvent e) {
        animations.remove();
        var anim = animations.peek();
        if (anim != null) {
            anim.setOnFinished(this::onMoveFinished);
            anim.play();
        }
    }

    @Override
    public void notify(Contestant contestant, Submit submit) {
        Platform.runLater(() -> {
            synchronized (this) {
                if (submit.getTime().isBefore(conf.startTime.plusMinutes(conf.liveDuration))) {
                    var trans = new ParallelTransition();
                    for (var liveCon : ranking)
                        trans.getChildren().add(liveCon.moveTo(getContestantY(liveCon)));
                    var liveCon = liveCons.get(contestant);
                    trans.getChildren().add(liveCon.update(submit));
                    animations.add(trans);
                    if (animations.size() == 1) {
                        trans.setOnFinished(this::onMoveFinished);
                        trans.play();
                    }
                }
            }
        });
    }
}
