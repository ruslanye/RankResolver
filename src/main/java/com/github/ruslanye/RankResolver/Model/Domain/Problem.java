package com.github.ruslanye.RankResolver.Model.Domain;

public class Problem {
    private final String id;
    private final int score;
    private Contestant solvedFirst;

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

    public boolean solvedFirst(Contestant contestant){
        if(solvedFirst != null)
            return contestant.equals(solvedFirst);
        if(contestant.getSolution(this) != null){
            solvedFirst = contestant;
            return true;
        }
        return false;
    }
}
