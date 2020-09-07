package com.github.ruslanye.RankResolver.Model.Graphics;

import com.github.ruslanye.RankResolver.Model.Domain.Contest;
import com.github.ruslanye.RankResolver.Model.Domain.Contestant;
import com.github.ruslanye.RankResolver.Model.Domain.Problem;
import com.github.ruslanye.RankResolver.Model.Domain.Submit;
import com.github.ruslanye.RankResolver.Model.Utils.Config;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class LiveContestant extends HBox {
    private static final double BOX_HEIGHT_RATIO = 0.95;
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
    private boolean resolved;
    private ListIterator<Problem> problemIter;
    private int currentScore;
    private long currentTime;

    public LiveContestant(Contestant contestant, Contest contest, double width, double height) {
        this.contestant = contestant;
        this.contest = contest;
        this.height = height;
        this.width = width;

        conf = Config.getConfig();

        rank = new TextBox("", Font.font(conf.fontSize));
        name = new TextBox(contestant.getName(), Font.font(conf.fontSize));
        score = new TextBox(String.valueOf(contestant.getFrozenScore()), Font.font(conf.fontSize), Pos.CENTER);
        placeHolder1 = new TextBox("");
        time = new TextBox(String.valueOf(TimeUnit.MILLISECONDS.toMinutes(contestant.getFrozenTotalTime())),
                Font.font(conf.fontSize), Pos.CENTER);
        placeHolder2 = new TextBox("");

        getChildren().addAll(rank, name, score, placeHolder1, time, placeHolder2);

        problems = new HashMap<>();
        for (var problem : contest.getProblems()) {
            var liveProblem = new LiveProblem(contestant, problem, conf.boxWidth, height * BOX_HEIGHT_RATIO);
            getChildren().add(liveProblem);
            problems.put(problem, liveProblem);
        }

        setAlignment(Pos.CENTER_LEFT);

        updateWidth(width);
        updateHeight(height);

        resolved = false;

        problemIter = contest.getProblems().listIterator();
        currentScore = contestant.getFrozenScore();
        currentTime = contestant.getFrozenTotalTime();
    }

    public Contestant getContestant() {
        return contestant;
    }

    public Animation moveTo(double y) {
        return new Timeline(new KeyFrame(conf.rankingMoveDuration, new KeyValue(layoutYProperty(), y)));
    }

    public void update(Submit submit) {
        score.getText().setText(String.valueOf(contestant.getFrozenScore()));
        time.getText().setText(String.valueOf(TimeUnit.MILLISECONDS.toMinutes(contestant.getFrozenTotalTime())));
        var problem = submit.getProblem();
        currentTime = contestant.getFrozenTotalTime();
        currentScore = contestant.getFrozenScore();
        problems.get(problem).update(submit);
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
        rank.updateHeight(height * BOX_HEIGHT_RATIO);
        name.updateHeight(height * BOX_HEIGHT_RATIO);
        score.updateHeight(height * BOX_HEIGHT_RATIO);
        placeHolder1.updateHeight(height * BOX_HEIGHT_RATIO);
        time.updateHeight(height * BOX_HEIGHT_RATIO);
        placeHolder2.updateHeight(height * BOX_HEIGHT_RATIO);
        for (var problem : problems.values())
            problem.updateHeight(height * BOX_HEIGHT_RATIO);
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

    public void resolve(List<Animation> forwards, List<Animation> backwards, LiveContestant next) {
        while (problemIter.hasNext()) {
            var problem = problemIter.next();
            if (contestant.getSubmits(problem).size() - contestant.getFrozenSubmits(problem).size() <= 0
                    || contestant.getFrozenSolution(problem) != null)
                continue;
            var liveProblem = problems.get(problem);
            forwards.add(doAction(e -> liveProblem.select()));
            backwards.add(doAction(e -> liveProblem.unselect()));
            int beforeScore = currentScore;
            long beforeTime = currentTime;
            if (contestant.getSolution(problem) != null) {
                currentScore += problem.getScore();
                currentTime += contestant.getTime(problem);
            }
            int tempScore = currentScore;
            long tempTime = currentTime;
            forwards.add(doAction(e -> {
                liveProblem.unfreeze();
                score.getText().setText(String.valueOf(tempScore));
                time.getText().setText(String.valueOf(TimeUnit.MILLISECONDS.toMinutes(tempTime)));
            }));
            backwards.add(doAction(e -> {
                liveProblem.update();
                score.getText().setText(String.valueOf(beforeScore));
                time.getText().setText(String.valueOf(TimeUnit.MILLISECONDS.toMinutes(beforeTime)));
            }));
            forwards.add(doAction(e -> liveProblem.unselect()));
            backwards.add(doAction(e -> liveProblem.select()));
            if (problemIter.hasNext() &&
                    (next.getScore() < currentScore || (next.getScore() == currentScore && next.getTime() > currentTime)))
                return;
        }
        resolved = true;
    }

    private Animation doAction(EventHandler<ActionEvent> onFinished) {
        return new Timeline(new KeyFrame(Duration.millis(1), onFinished));
    }

    public int getScore() {
        return currentScore;
    }

    public long getTime() {
        return currentTime;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void reset() {
        resolved = false;
        problemIter = contest.getProblems().listIterator();
        currentScore = contestant.getFrozenScore();
        currentTime = contestant.getFrozenTotalTime();
        score.getText().setText(String.valueOf(contestant.getFrozenScore()));
        time.getText().setText(String.valueOf(TimeUnit.MILLISECONDS.toMinutes(contestant.getFrozenTotalTime())));
        for(var problem : problems.values()){
            problem.update();
        }
    }
}
