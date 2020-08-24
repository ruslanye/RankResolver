package com.github.ruslanye.RankResolver.Model.Graphics;

import com.github.ruslanye.RankResolver.Model.Domain.Contestant;
import com.github.ruslanye.RankResolver.Model.Domain.Problem;
import com.github.ruslanye.RankResolver.Model.Domain.Submit;
import com.github.ruslanye.RankResolver.Model.Utils.Config;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.time.Duration;

public class LiveProblem extends StackPane {
    private final Rectangle background;
    private final VBox box;
    private final Text count;
    private final Text time;
    private final Contestant contestant;
    private final Problem problem;
    private final Config conf;

    public LiveProblem(Contestant contestant, Problem problem, Config conf, double width, double height) {
        this.contestant = contestant;
        this.problem = problem;
        this.conf = conf;
        background = new Rectangle(width, height);
        count = new Text("-");
        count.setFont(Font.font(conf.fontSize * 0.75));
        time = new Text();
        time.setFont(Font.font(conf.fontSize * 0.75));
        box = new VBox(count);
        getChildren().addAll(background, box);
        box.setAlignment(Pos.CENTER);
        update();
    }

    public void update(Submit s) {
        int beforeFreeze = contestant.countFrozenAttempts(problem);
        int afterFreeze = contestant.countAttempts(problem) - beforeFreeze;
        box.getChildren().remove(time);
        if (beforeFreeze + afterFreeze == 0) {
            count.setText("-");
            background.setFill(Color.TRANSPARENT);
            return;
        }
        var solution = contestant.getFrozenSolution(problem);
        if (setSolved(beforeFreeze, solution)) return;
        if (afterFreeze == 0) {
            count.setText(String.valueOf(beforeFreeze));
            if(s != null && s.getStatus().isQUE())
                background.setFill(conf.queuedColor);
            else
                background.setFill(conf.failedColor);
            return;
        }
        count.setText(beforeFreeze + "+" + afterFreeze);
        background.setFill(conf.queuedColor);
    }

    public void update(){
        update(null);
    }

    public void unfreeze() {
        int attempts = contestant.countAttempts(problem);
        box.getChildren().remove(time);
        if (attempts == 0) {
            count.setText("-");
            background.setFill(Color.TRANSPARENT);
            return;
        }
        var solution = contestant.getSolution(problem);
        if (setSolved(attempts, solution)) return;
        count.setText(String.valueOf(attempts));
        background.setFill(conf.failedColor);
    }

    private boolean setSolved(int attempts, Submit solution) {
        if (solution != null) {
            count.setText(String.valueOf(attempts));
            if (problem.solvedFirst(contestant))
                background.setFill(conf.solvedFirstColor);
            else
                background.setFill(conf.solvedColor);
            var duration = Duration.between(conf.startTime, solution.getTime());
            time.setText(String.format("(%d:%02d)", duration.toHoursPart(), duration.toMinutesPart()));
            box.getChildren().add(time);
            return true;
        }
        return false;
    }

    public void updateHeight(double height){
        background.setHeight(height);
    }

    public void updateWidth(double width){

    }

    public void select(){
        background.setStroke(conf.selectedColor);
        background.setStrokeWidth(2);
    }

    public void unselect(){
        background.setStroke(Color.TRANSPARENT);
        background.setStrokeWidth(0);
    }
}
