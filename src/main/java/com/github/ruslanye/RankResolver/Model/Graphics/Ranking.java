package com.github.ruslanye.RankResolver.Model.Graphics;

import com.github.ruslanye.RankResolver.Model.Domain.Contest;
import com.github.ruslanye.RankResolver.Model.Domain.Contestant;
import com.github.ruslanye.RankResolver.Model.Utils.Config;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Ranking extends Stage {
    protected static final Double MARGIN = 0.01;
    protected final Contest contest;
    protected final Config conf;
    protected final Pane pane;
    protected final Pane background;
    protected final List<Rectangle> rows;
    protected final List<LiveContestant> ranking;
    protected final RankingHeader header;

    public Ranking(Contest contest, Config conf) {
        this.contest = contest;
        this.conf = conf;
        pane = new Pane();
        background = new Pane();
        var stack = new StackPane(background, pane);
        Scene scene = new Scene(stack);
        setScene(scene);
        setWidth(conf.rankingWidth);
        setHeight(conf.rankingHeight);
        List<Contestant> frozenContestants = new ArrayList<>(contest.getContestants());
        Contest.frozenRank(frozenContestants);
        ranking = new ArrayList<>();
        rows = new ArrayList<>();
        header = new RankingHeader(contest, conf, getRowWidth(), getRowHeight());
        background.getChildren().add(header);
        for (int i = 0; i < frozenContestants.size(); i++) {
            var liveCon = new LiveContestant(frozenContestants.get(i), contest, conf, getRowWidth(), getRowHeight());
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
        this.widthProperty().addListener((obs, oldVal, newVal) -> {
            updateWidth();
        });
        this.heightProperty().addListener((obs, oldVal, newVal) -> {
            updateHeight();
        });

        updateWidth();
        updateHeight();
    }

    protected void updateWidth(){
        header.setLayoutX(getRowX());
        header.updateWidth(getRowWidth());
        for(int i = 0; i < ranking.size(); i++){
            var liveCon = ranking.get(i);
            liveCon.setLayoutX(getRowX());
            liveCon.updateWidth(getRowWidth());
            var rect = rows.get(i);
            rect.setLayoutX(getRowX());
            rect.setWidth(getRowWidth());
        }
    }

    protected void updateHeight(){
        header.setLayoutY(getRowY(0));
        header.updateHeight(getRowHeight());
        for(int i = 0; i < ranking.size(); i++){
            var liveCon = ranking.get(i);
            liveCon.setLayoutY(getRowY(i+1));
            liveCon.updateHeight(getRowHeight());
            var rect = rows.get(i);
            rect.setLayoutY(getRowY(i+1));
            rect.setHeight(getRowHeight());
        }
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
