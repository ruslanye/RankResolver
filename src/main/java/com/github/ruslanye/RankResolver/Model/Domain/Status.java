package com.github.ruslanye.RankResolver.Model.Domain;

import java.util.Objects;

public class Status {
    private final String id;
    private final long penalty;
    private final boolean isOK;
    private final boolean isQUE;

    public Status(String id, long penalty){
        this.id = id.toUpperCase();
        this.penalty = penalty;
        this.isOK = id.equals("OK");
        this.isQUE = id.equals("QUE");
    }

    public boolean isOK() {
        return isOK;
    }

    public boolean isQUE() {
        return isQUE;
    }

    public long getPenalty() {
        return penalty;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Status)) return false;
        Status status1 = (Status) o;
        return Objects.equals(id, status1.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
