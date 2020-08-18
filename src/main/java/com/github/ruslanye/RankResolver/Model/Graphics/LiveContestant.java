package com.github.ruslanye.RankResolver.Model.Graphics;

import com.github.ruslanye.RankResolver.Model.Domain.Contest;
import com.github.ruslanye.RankResolver.Model.Domain.Contestant;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LiveContestant extends StackPane {
    private final Contestant contestant;
    private final Contest contest;
    private final Config conf;
    private final Rectangle background;
    private final HBox box;
    private final List<Text> scores;
    private final Text totalScore;
    private final Text time;

    public LiveContestant(Contestant contestant, Contest contest, Config conf) {
        this.contestant = contestant;
        this.contest = contest;
        this.conf = conf;
        scores = new ArrayList<>();
        totalScore = new Text(" 0 ");
        time = new Text(" 0 ");
        box = new HBox();
        box.getChildren().addAll(new Text(contestant.getName()));
        box.setAlignment(Pos.CENTER);
        for(var ignored : contest.getProblems())
            scores.add(new Text(" - "));
        box.getChildren().addAll(scores);
        box.getChildren().addAll(totalScore, time);
        background = new Rectangle();
        background.setWidth(conf.liveSubmitWidth);
        background.setHeight(conf.liveSubmitHeight);
        background.setFill(Color.TRANSPARENT);
        background.setStroke(Color.BLACK);
        setPrefHeight(conf.liveSubmitHeight);
        setPrefWidth(conf.liveSubmitWidth);
        getChildren().addAll(background, box);
    }

    public Contestant getContestant(){
        return contestant;
    }

    public Animation moveTo(double y) {
        return new Timeline(new KeyFrame(Duration.millis(conf.liveRankingMoveDuration), new KeyValue(layoutYProperty(), y)));
    }

    public Animation update(Submit s){
        totalScore.setText(String.valueOf(contestant.getScore()));
        time.setText(" " + TimeUnit.MILLISECONDS.toMinutes(contestant.getTotalTime()));
        return new ParallelTransition();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LiveContestant)) return false;
        LiveContestant that = (LiveContestant) o;
        return Objects.equals(contestant.getName(), that.contestant.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(contestant.getName());
    }
}
