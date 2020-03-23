package com.github.ruslanye.RankReslover.Model.Domain;

import java.util.*;

public class Contestant implements SubmitObserver {
    private String name;
    private int currentScore;
    private List<ContestantObserver> observers;
    private Map<Problem, HashSet<Submit>> groupedSubmits;
    private Map<Problem, Submit> solvedProblems;

    public Contestant(String name) {
        this.name = name;
        currentScore = 0;
        observers = new ArrayList<>();
        groupedSubmits = new HashMap<>();
        solvedProblems = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return currentScore;
    }

    public void addObserver(ContestantObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(ContestantObserver observer) {
        observers.remove(observer);
    }

    public Set<Submit> getSubmits(Problem problem) {
        return Collections.unmodifiableSet(groupedSubmits.get(problem));
    }

    public void addSubmit(Submit submit) {
        var problem = submit.getProblem();
        var submits = groupedSubmits.computeIfAbsent(problem, k -> new HashSet<>());
        submits.add(submit);
        if (submit.getStatus() == Status.OK && solvedProblems.get(problem) == null) {
            solvedProblems.put(problem, submit);
            currentScore += problem.getScore(submit.getTimestamp());
        }
        for (var observer : observers) {
            observer.notify(this, submit);
        }
    }

    @Override
    public void notify(Submit submit, Status oldStatus) {
        var problem = submit.getProblem();
        var submits = groupedSubmits.get(problem);
        if (submit.getStatus() != oldStatus && oldStatus == Status.OK) {
            if (solvedProblems.get(problem) == submit) {
                boolean found = false;
                for (var sub : submits) {
                    if (sub != submit && sub.getStatus() == Status.OK) {
                        solvedProblems.put(problem, sub);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    currentScore -= problem.getScore(submit.getTimestamp());
                }
            }
        }
        if (submit.getStatus() == Status.OK && solvedProblems.get(problem) == null) {
            solvedProblems.put(problem, submit);
            currentScore += problem.getScore(submit.getTimestamp());
        }

        for (var observer : observers) {
            observer.notify(this, submit);
        }
    }

}
