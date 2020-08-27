package com.github.ruslanye.RankResolver.Model.Domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Contestant implements SubmitObserver {
    private final String name;
    private final String misc;
    private final LocalDateTime freezeTime;
    private final Calculator calculator;
    private final Calculator frozenCalculator;
    private final List<ContestantObserver> observers;

    public Contestant(String name, String misc, LocalDateTime startTime, LocalDateTime freezeTime) {
        this.name = name;
        this.misc = misc;
        this.freezeTime = freezeTime;
        observers = new ArrayList<>();
        calculator = new Calculator(startTime);
        frozenCalculator = new Calculator(startTime);
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return calculator.getScore();
    }

    public int getFrozenScore() {
        return frozenCalculator.getScore();
    }

    public long getTime() {
        return calculator.getTime();
    }

    public long getFrozenTime() {
        return frozenCalculator.getTime();
    }

    public void addObserver(ContestantObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(ContestantObserver observer) {
        observers.remove(observer);
    }

    public List<Submit> getSubmits(Problem problem) {
        return calculator.getSubmits(problem);
    }

    public List<Submit> getFrozenSubmits(Problem problem) {
        return frozenCalculator.getSubmits(problem);
    }

    public Submit getSolution(Problem problem) {
        return calculator.getSolution(problem);
    }

    public Submit getFrozenSolution(Problem problem) {
        return frozenCalculator.getSolution(problem);
    }

    public int countAttempts(Problem problem) {
        return calculator.countAttempts(problem);
    }

    public int countFrozenAttempts(Problem problem) {
        return frozenCalculator.countAttempts(problem);
    }

    @Override
    public void addSubmit(Submit submit) {
        if (!submit.getContestant().equals(this))
            return;
        submit.addObserver(this);
        calculator.addSubmit(submit);
        if (submit.getTime().isBefore(freezeTime))
            frozenCalculator.addSubmit(submit);
        for (var observer : observers) {
            observer.notify(this, submit);
        }
    }

    @Override
    public void notify(Submit submit, Status oldStatus) {
        calculator.updateSubmit(submit, oldStatus);
        if (submit.getTime().isBefore(freezeTime))
            frozenCalculator.updateSubmit(submit, oldStatus);
        for (var observer : observers) {
            observer.notify(this, submit);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contestant that = (Contestant) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public String getMisc() {
        return misc;
    }
}
