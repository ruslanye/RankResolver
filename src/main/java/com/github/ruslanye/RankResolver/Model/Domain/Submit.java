package com.github.ruslanye.RankResolver.Model.Domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Submit {
    private long number;
    private Status status;
    private Problem problem;
    private LocalDateTime time;
    private Contestant contestant;
    private List<SubmitObserver> observers;

    public Submit(long number, Contestant contestant, Problem problem, LocalDateTime time, Status status) {
        this.number = number;
        this.contestant = contestant;
        this.problem = problem;
        this.time = time;
        this.status = status;
        observers = new ArrayList<>();
    }

    public void changeStatus(Status newStatus) {
        var oldStatus = status;
        status = newStatus;
        for (var observer : observers) {
            observer.notify(this, oldStatus);
        }
    }

    public long getNumber() {
        return number;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public Problem getProblem() {
        return problem;
    }

    public Contestant getContestant() {
        return contestant;
    }

    public void addObserver(SubmitObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(SubmitObserver observer) {
        observers.remove(observer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Submit)) return false;
        Submit submit = (Submit) o;
        return number == submit.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
