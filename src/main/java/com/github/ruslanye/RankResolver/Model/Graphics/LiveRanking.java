package com.github.ruslanye.RankResolver.Model.Graphics;

import com.github.ruslanye.RankResolver.Model.Domain.Contest;
import com.github.ruslanye.RankResolver.Model.Domain.Contestant;
import com.github.ruslanye.RankResolver.Model.Domain.ContestantObserver;
import com.github.ruslanye.RankResolver.Model.Domain.Submit;
import com.github.ruslanye.RankResolver.Model.Utils.Config;
import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class LiveRanking extends Ranking implements ContestantObserver {
    private final Map<Contestant, LiveContestant> liveCons = new HashMap<>();
    private final Queue<Animation> animations = new LinkedList<>();
    private Animation autoscroll;

    public LiveRanking(Contest contest) {
        super(contest);
        setTitle("LiveRanking");
        for (var liveCon : ranking) {
            liveCons.put(liveCon.getContestant(), liveCon);
        }
        for (int i = 0; i < ranking.size(); i++) {
            var liveCon = ranking.get(i);
            liveCon.setLayoutX(getRowX());
            liveCon.setLayoutY(getRowY(i + 1));
            liveCon.setRank(i + 1);
        }

        this.widthProperty().addListener((obs, oldVal, newVal) -> {
            updateWidth();
        });
        this.heightProperty().addListener((obs, oldVal, newVal) -> {
            updateHeight();
        });
    }

    @Override
    protected void updateHeight() {
        super.updateHeight();
        while (animations != null && animations.size() > 0)
            animations.poll().stop();
        if (autoscroll != null) {
            autoscroll.stop();
        }
        autoscroll = makeAutoscroll();
        autoscroll.play();
    }

    private Animation makeAutoscroll() {
        SequentialTransition trans = new SequentialTransition(new PauseTransition(conf.autoscrollDelay.divide(2)),
                moveToEnd(ranking.size(), conf.autoscrollDuration),
                new PauseTransition(conf.autoscrollDelay.divide(2)));
        trans.setAutoReverse(true);
        trans.setCycleCount(Animation.INDEFINITE);
        trans.setDelay(conf.autoscrollDelay.divide(2));
        return trans;
    }

    private synchronized void onMoveFinished(ActionEvent e) {
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
                liveCons.get(contestant).update(submit);
                if (submit.getTime().isBefore(conf.startTime.plusMinutes(conf.liveDuration))) {
                    reRank();
                    var trans = new ParallelTransition();
                    for (int i = 0; i < ranking.size(); i++) {
                        var liveCon = ranking.get(i);
                        liveCon.setRank(i + 1);
                        trans.getChildren().add(liveCon.moveTo(getRowY(i + 1)));
                    }
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
