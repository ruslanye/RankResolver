package com.github.ruslanye.RankResolver.Model.Domain;

public enum Status {
    OK, RTE, MEM, TLE, ANS, CME, EXT, INT, QUE, REJ;
    private long penalty;

    public long getPenalty() {
        return penalty;
    }

    public void setPenalty(long penalty) {
        this.penalty = penalty;
    }
}
