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

import java.util.*;

public class LiveRanking extends Ranking implements ContestantObserver {
    private final Map<Contestant, LiveContestant> liveCons;
    private final Queue<Animation> animations;

    public LiveRanking(Contest contest, Config conf) {
        super(contest, conf);
        animations = new LinkedList<>();
        setTitle("LiveRanking");
        liveCons = new HashMap<>();
        for(var liveCon : ranking){
            liveCons.put(liveCon.getContestant(), liveCon);
        }
        for(int i = 0; i < ranking.size(); i++){
            var liveCon = ranking.get(i);
            liveCon.setLayoutX(getRowX());
            liveCon.setLayoutY(getRowY(i+1));
            liveCon.setRank(i+1);
        }
    }

    private void reRank() {
        ranking.sort((x, y) -> {
            var con1 = x.getContestant();
            var con2 = y.getContestant();
            var scoreComp = Long.compare(con2.getScore(), con1.getScore());
            var timeComp = Long.compare(con1.getTime(),
                    con2.getTime());
            return scoreComp == 0 ? timeComp == 0 ? con1.getName().compareTo(con2.getName()) : timeComp : scoreComp;
        });
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
                if (submit.getTime().isBefore(conf.startTime.plusMinutes(conf.liveDuration))) {
                    reRank();
                    var trans = new ParallelTransition();
                    for(int i = 0; i < ranking.size(); i++){
                        var liveCon = ranking.get(i);
                        liveCon.setRank(i+1);
                        trans.getChildren().add(liveCon.moveTo(getRowY(i+1)));
                    }
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
