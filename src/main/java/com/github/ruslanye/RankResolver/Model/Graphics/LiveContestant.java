package com.github.ruslanye.RankResolver.Model.Graphics;

import com.github.ruslanye.RankResolver.Model.Domain.Contest;
import com.github.ruslanye.RankResolver.Model.Domain.Contestant;
import com.github.ruslanye.RankResolver.Model.Domain.Problem;
import com.github.ruslanye.RankResolver.Model.Domain.Submit;
import com.github.ruslanye.RankResolver.Model.Utils.Config;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LiveContestant extends HBox {
    private static final double PROBLEM_HEIGHT_RATIO = 0.95;
    private final Contestant contestant;
    private final Contest contest;
    private final Config conf;
    private final Map<Problem, LiveProblem> problems;
    private final TextBox rank;
    private final TextBox name;
    private final TextBox score;
    private final TextBox placeHolder1;
    private final TextBox time;
    private final TextBox placeHolder2;
    private double width;
    private double height;

    public LiveContestant(Contestant contestant, Contest contest, Config conf, double width, double height) {
        this.contestant = contestant;
        this.contest = contest;
        this.conf = conf;
        this.height = height;
        this.width = width;

        rank = new TextBox("", Font.font(conf.fontSize));
        name = new TextBox(contestant.getName(), Font.font(conf.fontSize));
        score = new TextBox(String.valueOf(contestant.getFrozenScore()), Font.font(conf.fontSize), Pos.CENTER);
        placeHolder1 = new TextBox("");
        time = new TextBox(String.valueOf(TimeUnit.MILLISECONDS.toMinutes(contestant.getFrozenTime())),
                Font.font(conf.fontSize), Pos.CENTER);
        placeHolder2 = new TextBox("");

        getChildren().addAll(rank, name, score, placeHolder1, time, placeHolder2);

        problems = new HashMap<>();
        for (var problem : contest.getProblems()) {
            var liveProblem = new LiveProblem(contestant, problem, conf, conf.boxWidth, height * PROBLEM_HEIGHT_RATIO);
            getChildren().add(liveProblem);
            problems.put(problem, liveProblem);
        }

        setAlignment(Pos.CENTER_LEFT);

        updateWidth(width);
        updateHeight(height);
    }

    public Contestant getContestant() {
        return contestant;
    }

    public Animation moveTo(double y) {
        return new Timeline(new KeyFrame(Duration.millis(conf.liveRankingMoveDuration), new KeyValue(layoutYProperty(), y)));
    }

    public Animation update(Submit submit) {
        score.getText().setText(String.valueOf(contestant.getFrozenScore()));
        time.getText().setText(" " + TimeUnit.MILLISECONDS.toMinutes(contestant.getFrozenTime()));
        var problem = submit.getProblem();
        problems.get(problem).update(contestant, problem, conf);
        return new ParallelTransition();
    }

    public void setRank(int pos) {
        rank.getText().setText(String.valueOf(pos));
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

    public void updateHeight(double newHeight) {
        height = newHeight;
        for (var problem : problems.values())
            problem.updateHeight(height * PROBLEM_HEIGHT_RATIO);
        setPrefHeight(height);
    }

    public void updateWidth(double newWidth) {
        double boxWidth = conf.boxWidth * newWidth / width;
        width = newWidth;
        rank.updateWidth(boxWidth);
        name.updateWidth(width - (contest.getProblems().size() + 5) * boxWidth * 1.1);
        score.updateWidth(boxWidth);
        placeHolder1.updateWidth(boxWidth);
        time.updateWidth(boxWidth);
        placeHolder2.updateWidth(boxWidth);
        setSpacing(boxWidth / 10);
        for (var problem : problems.values())
            problem.updateWidth(boxWidth);
        setPrefWidth(width);
    }
}
