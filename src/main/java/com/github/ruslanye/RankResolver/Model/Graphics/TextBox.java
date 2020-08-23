package com.github.ruslanye.RankResolver.Model.Graphics;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TextBox extends StackPane {
    private final Text text;
    private final Rectangle background;

    public TextBox(String text){
        this(text, Font.getDefault());
    }

    public TextBox(String text, Font font){
        this(text, font, Pos.CENTER_LEFT);
    }

    public TextBox(String text, Font font, Pos pos){
        this.text = new Text(text);
        this.text.setFont(font);
        background = new Rectangle();
        background.setFill(Color.TRANSPARENT);
        setAlignment(pos);
        getChildren().addAll(background, this.text);
    }

    public Text getText() {
        return text;
    }

    public void setFill(Paint p){
        background.setFill(p);
    }

    public void updateWidth(double width){
        background.setWidth(width);
    }

    public void updateHeight(double height){
        background.setHeight(height);
    }
}
