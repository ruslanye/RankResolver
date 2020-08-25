package com.github.ruslanye.RankResolver.Model.Graphics;

import com.github.ruslanye.RankResolver.Model.Domain.Contest;
import com.github.ruslanye.RankResolver.Model.Utils.Config;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public class RankingHeader extends StackPane {
    private final Contest contest;
    private final Config conf;
    private final HBox box;
    private final Rectangle background;
    private final TextBox rank;
    private final TextBox name;
    private final TextBox score;
    private final TextBox placeHolder1;
    private final TextBox time;
    private final TextBox placeHolder2;
    private final List<TextBox> problems;
    private double width;
    private double height;

    public RankingHeader(Contest contest, double width, double height) {
        this.contest = contest;
        this.width = width;
        this.height = height;

        conf = Config.getConfig();

        box = new HBox();
        background = new Rectangle(width, height);

        background.setFill(conf.headerColor);
        background.setStroke(Color.BLACK);

        getChildren().addAll(background, box);

        rank = new TextBox("Rank", getFont(conf.fontSize));
        name = new TextBox("Name", getFont(conf.fontSize));
        score = new TextBox("Score", getFont(conf.fontSize));
        placeHolder1 = new TextBox("");
        time = new TextBox("Time", getFont(conf.fontSize));
        placeHolder2 = new TextBox("");

        box.getChildren().addAll(rank, name, score, placeHolder1, time, placeHolder2);

        problems = new ArrayList<>();

        for (var problem : contest.getProblems()) {
            var text = new TextBox(problem.getId(), getFont(conf.fontSize));
            text.setAlignment(Pos.CENTER);
            problems.add(text);
            box.getChildren().add(text);
        }
        setAlignment(Pos.CENTER_LEFT);

        updateWidth(width);
        updateHeight(height);
    }

    public void updateWidth(double newWidth) {
        double boxWidth = conf.boxWidth * newWidth / width;
        width = newWidth;
        background.setWidth(width);
        rank.updateWidth(boxWidth);
        name.updateWidth(width - (contest.getProblems().size() + 5) * boxWidth * 1.1);
        score.updateWidth(boxWidth);
        placeHolder1.updateWidth(boxWidth);
        time.updateWidth(boxWidth);
        placeHolder2.updateWidth(boxWidth);
        box.setSpacing(conf.boxWidth / 10);
        for (var problem : problems) {
            problem.setMinWidth(boxWidth);
        }
        setPrefWidth(width);
    }

    public void updateHeight(double newHeight) {
        height = newHeight;
        background.setHeight(height);
        setPrefHeight(height);
    }

    private Font getFont(double fontSize) {
        return Font.font(Font.getDefault().getName(), FontWeight.BOLD, fontSize);
    }
}
