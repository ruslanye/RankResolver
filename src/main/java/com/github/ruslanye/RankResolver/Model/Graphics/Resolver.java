package com.github.ruslanye.RankResolver.Model.Graphics;

import com.github.ruslanye.RankResolver.Model.Domain.Contest;
import com.github.ruslanye.RankResolver.Model.Domain.Contestant;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class Resolver extends Ranking {
    private final List<Animation> forwards;
    private final List<Animation> backwards;
    private int nextAnim;
    private int prevAnim;
    private boolean playing;

    public Resolver(Contest contest) {
        super(contest);
        setTitle("Resolver");
        this.widthProperty().addListener((obs, oldVal, newVal) -> updateWidth());
        this.heightProperty().addListener((obs, oldVal, newVal) -> updateHeight());
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

    private void announceWinner(Contestant con, int pos) {
        WebView view = new WebView();
        view.getEngine().loadContent(conf.winScreen.replaceAll("@name",
                con.getName()).replaceAll("@misc", con.getMisc()));
        var box = new VBox(view);
        box.setAlignment(Pos.CENTER);
        var rect = new Rectangle(getWidth(), getHeight());
        rect.setFill(Color.WHITE);
        var stack = new StackPane(rect, box);
        stack.setLayoutY(-getMoveY(pos));
        forwards.add(new Timeline(new KeyFrame(Duration.millis(1), (e -> pane.getChildren().add(stack)))));
        backwards.add(new Timeline(new KeyFrame(Duration.millis(1), (e -> pane.getChildren().remove(stack)))));
        forwards.add(new Timeline(new KeyFrame(Duration.millis(1), (e -> pane.getChildren().remove(stack)))));
        backwards.add(new Timeline(new KeyFrame(Duration.millis(1), (e -> pane.getChildren().add(stack)))));

    }

    public void prepareResolver() {
        forwards.clear();
        backwards.clear();
        forwards.add(moveToEnd(ranking.size(), conf.autoscrollDuration));
        backwards.add(moveToEnd(0, conf.autoscrollDuration));
        int i = ranking.size() - 1;
        var prevTrans = updateRanking();
        while (i >= 0) {
            var liveCon = ranking.get(i);
            forwards.add(selectRow(i));
            backwards.add(unselectRow(i));
            if (!liveCon.isFrozen()) {
                forwards.add(unselectRow(i));
                backwards.add(selectRow(i));
                if (i < conf.winnersNumber)
                    announceWinner(liveCon.getContestant(), i + 1);
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
            if (liveCon == ranking.get(i)) {
                if (i < conf.winnersNumber)
                    announceWinner(liveCon.getContestant(), i + 1);
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
