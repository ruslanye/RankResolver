package com.github.ruslanye.RankResolver.Model.Graphics;

import com.github.ruslanye.RankResolver.Model.Domain.Submit;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class LiveSubmit extends StackPane {

    private final Text status;
    private final Rectangle background;
    private final Submit submit;
    private final HBox box;

    public LiveSubmit(Submit submit, double width, double height){
        this.submit = submit;
        box = new HBox();
        box.getChildren().addAll(new Text(submit.getContestant().getName()), new Text(" " + submit.getProblem().getId() + " "));
        status = new Text(submit.getStatus().getId());
//        box.getChildren().addAll(new Text(submit.getContestant().getName()));
        box.getChildren().add(status);
        background = new Rectangle();
        background.setWidth(width);
        background.setHeight(height);
        background.setFill(Color.TRANSPARENT);
        background.setStroke(Color.BLACK);
        setPrefHeight(height);
        setPrefWidth(width);
        getChildren().addAll(background, box);
    }

    public void updateStatus(){
        status.setText(submit.getStatus().getId());
    }

    public Submit getSubmit(){
        return submit;
    }
}
