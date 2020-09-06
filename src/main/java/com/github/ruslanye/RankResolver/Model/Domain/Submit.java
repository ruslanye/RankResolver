package com.github.ruslanye.RankResolver.Model.Domain;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Submit {
    private final long number;
    private final Problem problem;
    private final LocalDateTime time;
    private final Contestant contestant;
    private final List<SubmitObserver> observers;
    private Status status;

    public Submit(long number, Contestant contestant, Problem problem, LocalDateTime time, Status status) {
        this.number = number;
        this.contestant = contestant;
        this.problem = problem;
        this.time = time;
        this.status = status;
        observers = new LinkedList<>();
        if (status.isOK())
            problem.solvedFirst(contestant);
    }

    public long getNumber() {
        return number;
    }

    public void changeStatus(Status newStatus) {
        var oldStatus = status;
        status = newStatus;
        if (status.isOK())
            problem.solvedFirst(contestant);
        for (var observer : new LinkedList<>(observers)) {
            observer.notify(this, oldStatus);
        }
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

    public synchronized void addObserver(SubmitObserver observer) {
        observers.add(observer);
    }

    public synchronized void removeObserver(SubmitObserver observer) {
        observers.remove(observer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Submit)) return false;
        Submit submit = (Submit) o;
        return number == submit.number &&
                Objects.equals(status, submit.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    public Contestant getContestant() {
        return contestant;
    }
}
