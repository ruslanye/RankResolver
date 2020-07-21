package com.github.ruslanye.RankResolver.Model.Domain;

import java.util.Objects;

public class Status {
    private final String status;
    private final long penalty;
    private final boolean isOK;

    public Status(String status, long penalty){
        this.status = status.toUpperCase();
        this.penalty = penalty;
        this.isOK = status.equals("OK");
    }

    public boolean isOK() {
        return isOK;
    }

    public long getPenalty() {
        return penalty;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Status)) return false;
        Status status1 = (Status) o;
        return Objects.equals(status, status1.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }
}
