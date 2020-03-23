package com.github.ruslanye.RankReslover.Model.Domain;

public class Problem {
    private int score;
    private String id;
    private String beginTimestamp;
    public Problem(String id, int score, String beginTimestamp) {
        this.id = id;
        this.score = score;
        this.beginTimestamp = beginTimestamp;
    }

    public String getId(){
        return id;
    }

    public int getScore(String timestamp){
        return this.score;
    }

    @Override
    public int hashCode(){
        return id.hashCode();
    }

}
