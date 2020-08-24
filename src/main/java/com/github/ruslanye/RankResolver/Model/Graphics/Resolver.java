package com.github.ruslanye.RankResolver.Model.Graphics;

import com.github.ruslanye.RankResolver.Model.Domain.Contest;
import com.github.ruslanye.RankResolver.Model.Domain.Contestant;
import com.github.ruslanye.RankResolver.Model.Utils.Config;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class Resolver extends Ranking {
    private final List<Animation> forwards;
    private final List<Animation> backwards;
    private int nextAnim;
    private int prevAnim;
    private boolean playing;

    public Resolver(Contest contest, Config conf) {
        super(contest, conf);
        setTitle("Resolver");
        this.widthProperty().addListener((obs, oldVal, newVal) -> {
            updateWidth();
        });
        this.heightProperty().addListener((obs, oldVal, newVal) -> {
            updateHeight();
        });
        getScene().addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            switch (e.getCode()) {
                case SPACE:
                    if (playing)
                        pause();
                    else
                        play();
                    break;
                case LEFT:
                    pause();
                    prevStep();
                    break;
                case RIGHT:
                    pause();
                    nextStep();
                    break;
            }
        });
        nextAnim = 0;
        prevAnim = -1;
        playing = false;
        forwards = new ArrayList<>();
        backwards = new ArrayList<>();
        prepareResolver();
    }

    private Animation selectRow(int pos) {
        return new Timeline(new KeyFrame(Duration.millis(1), (e -> {
            rows.get(pos).setStroke(conf.selectedColor);
            rows.get(pos).setStrokeWidth(2);
        })));
    }

    private Animation unselectRow(int pos) {
        return new Timeline(new KeyFrame(Duration.millis(1), (e -> {
            rows.get(pos).setStroke(Color.BLACK);
            rows.get(pos).setStrokeWidth(1);
        })));
    }

    private Animation updateRanking() {
        var trans = new ParallelTransition();
        for (int i = 0; i < ranking.size(); i++) {
            var liveCon = ranking.get(i);
            int finalI = i;
            trans.getChildren().add(new Timeline(new KeyFrame(Duration.millis(1), (e -> liveCon.setRank(finalI + 1)))));
            trans.getChildren().add(liveCon.moveTo(getRowY(i + 1)));
        }
        return trans;
    }

    private void announceWinner(Contestant con){

    }

    public void prepareResolver() {
        forwards.clear();
        backwards.clear();
        forwards.add(moveToEnd(ranking.size(), conf.autoscrollDuration));
        backwards.add(moveToEnd(0, conf.autoscrollDuration));
        int i = ranking.size() - 1;
//        reRank();
        var prevTrans = updateRanking();
        while (i >= 0) {
            var liveCon = ranking.get(i);
            forwards.add(selectRow(i));
            backwards.add(unselectRow(i));
            if (!liveCon.isFrozen()) {
                forwards.add(unselectRow(i));
                backwards.add(selectRow(i));
                if (i >= conf.rankingContestantsLimit) {
                    forwards.add(moveToEnd(i, conf.resolverStepDuration));
                    backwards.add(moveToEnd(i + 1, conf.resolverStepDuration));
                }
                i--;
                continue;
            }
            liveCon.setFrozen(false);
            forwards.addAll(liveCon.unfreeze());
            backwards.addAll(liveCon.freeze());
            forwards.add(unselectRow(i));
            backwards.add(selectRow(i));
            if (reRank()) {
                backwards.add(prevTrans);
                prevTrans = updateRanking();
                forwards.add(prevTrans);
            }
            if(liveCon == ranking.get(i)){
                if(i < conf.winnersNumber)
                    announceWinner(liveCon.getContestant());
                if (i >= conf.rankingContestantsLimit) {
                    forwards.add(moveToEnd(i, conf.resolverStepDuration));
                    backwards.add(moveToEnd(i + 1, conf.resolverStepDuration));
                }
                i--;
            }
        }
        for (LiveContestant liveContestant : ranking)
            liveContestant.setFrozen(true);
        reRank();
        updateHeight();
    }

    public void nextStep() {
        if (nextAnim >= forwards.size())
            return;
        nextAnim++;
        prevAnim++;
        forwards.get(nextAnim - 1).play();

    }

    public void prevStep() {
        if (prevAnim < 0)
            return;
        prevAnim--;
        nextAnim--;
        backwards.get(prevAnim + 1).play();
    }

    public void play() {
        playing = true;
        nextAnim++;
        prevAnim++;
        var anim = forwards.get(nextAnim - 1);
        anim.setOnFinished(this::keepPlaying);
        anim.play();
    }

    private void keepPlaying(ActionEvent e) {
        if (playing && nextAnim < forwards.size()) {
            nextAnim++;
            prevAnim++;
            var anim = forwards.get(nextAnim - 1);
            anim.setOnFinished(this::keepPlaying);
            anim.setDelay(conf.resolverStepDuration);
            anim.play();
        }
    }

    public void pause() {
        playing = false;
    }
}
