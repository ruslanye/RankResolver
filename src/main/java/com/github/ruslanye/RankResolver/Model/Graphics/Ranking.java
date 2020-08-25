package com.github.ruslanye.RankResolver.Model.Graphics;

import com.github.ruslanye.RankResolver.Model.Domain.Contest;
import com.github.ruslanye.RankResolver.Model.Domain.Contestant;
import com.github.ruslanye.RankResolver.Model.Utils.Config;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class Ranking extends Stage {
    protected static final Double MARGIN = 0.01;
    protected final Contest contest;
    protected final Config conf;
    protected final StackPane stack;
    protected final Pane pane;
    protected final Pane background;
    protected final List<Rectangle> rows;
    protected final List<LiveContestant> ranking;
    protected final RankingHeader header;

    public Ranking(Contest con) {
        contest = con;
        conf = Config.getConfig();
        pane = new Pane();
        background = new Pane();
        stack = new StackPane(background, pane);
        Scene scene = new Scene(stack);
        setScene(scene);
        setWidth(conf.rankingWidth);
        setHeight(conf.rankingHeight);
        List<Contestant> frozenContestants = new ArrayList<>(contest.getContestants());
        Contest.frozenRank(frozenContestants);
        ranking = new ArrayList<>();
        rows = new ArrayList<>();
        header = new RankingHeader(contest, getRowWidth(), getRowHeight());
        background.getChildren().add(header);
        for (int i = 0; i < frozenContestants.size(); i++) {
            var liveCon = new LiveContestant(frozenContestants.get(i), contest, getRowWidth(), getRowHeight());
            ranking.add(liveCon);
            pane.getChildren().add(liveCon);
            liveCon.setLayoutX(getRowX());
            liveCon.setLayoutY(getRowY(i + 1));
            liveCon.setRank(i + 1);
            Rectangle rect = new Rectangle(getRowWidth(), getRowHeight());
            rect.setLayoutX(getRowX());
            rect.setLayoutY(getRowY(i + 1));
            if (i % 2 == 0)
                rect.setFill(conf.rowColor1);
            else
                rect.setFill(conf.rowColor2);
            rect.setStroke(Color.BLACK);
            rows.add(rect);
            background.getChildren().add(rect);
        }

        updateWidth();
        updateHeight();
    }

    protected Animation moveTo(double y, Duration duration) {
        return new Timeline(new KeyFrame(duration, new KeyValue(stack.layoutYProperty(), y)));
    }

    protected Animation moveToEnd(int pos, Duration duration) {
        return moveTo(Math.min(getRowY(getLimit()) - getRowY(pos + 2), 0), duration);
    }

    protected void updateWidth() {
        header.setLayoutX(getRowX());
        header.updateWidth(getRowWidth());
        for (int i = 0; i < ranking.size(); i++) {
            var liveCon = ranking.get(i);
            liveCon.setLayoutX(getRowX());
            liveCon.updateWidth(getRowWidth());
            var rect = rows.get(i);
            rect.setLayoutX(getRowX());
            rect.setWidth(getRowWidth());
        }
    }

    protected void updateHeight() {
        header.setLayoutY(getRowY(0));
        header.updateHeight(getRowHeight());
        for (int i = 0; i < ranking.size(); i++) {
            var liveCon = ranking.get(i);
            liveCon.setLayoutY(getRowY(i + 1));
            liveCon.updateHeight(getRowHeight());
            var rect = rows.get(i);
            rect.setLayoutY(getRowY(i + 1));
            rect.setHeight(getRowHeight());
        }
    }

    protected boolean reRank() {
        var tempRanking = new ArrayList<>(ranking);
        ranking.sort((x, y) -> {
            var con1 = x.getContestant();
            var con2 = y.getContestant();
            var scoreComp = Long.compare(y.getScore(), x.getScore());
            var timeComp = Long.compare(x.getTime(),
                    y.getTime());
            return scoreComp == 0 ? timeComp == 0 ? con1.getName().compareTo(con2.getName()) : timeComp : scoreComp;
        });
        for (int i = 0; i < ranking.size(); i++)
            if (ranking.get(i) != tempRanking.get(i))
                return true;
        return false;
    }

    protected int getLimit() {
        return conf.rankingContestantsLimit + 2;
    }

    protected double getRowX() {
        return getWidth() * MARGIN;
    }

    protected double getRowY(int pos) {
        return getHeight() * MARGIN + getRowHeight() * pos;
    }

    protected double getRowHeight() {
        return getHeight() * (1 - 2 * MARGIN) / getLimit();
    }

    protected double getRowWidth() {
        return getWidth() * (1 - 2 * MARGIN);
    }
}
