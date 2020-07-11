package com.github.ruslanye.RankResolver.Model.Domain;

public class Problem {

    private final String id;
    private final int score;

    public Problem(String id, int score) {
        this.id = id;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
