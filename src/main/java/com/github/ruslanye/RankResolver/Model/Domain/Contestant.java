package com.github.ruslanye.RankResolver.Model.Domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Contestant implements SubmitObserver {
    private final String name;
    private final LocalDateTime startTime;
    private final List<ContestantObserver> observers;
    private final Map<Problem, Submit> solvedProblems;
    private final Map<Problem, Set<Submit>> groupedSubmits;
    private final Map<Problem, Long> penalties;
    private int score;
    private long totalTime;

    public Contestant(String name, LocalDateTime startTime) {
        score = 0;
        totalTime = 0;
        this.name = name;
        this.startTime = startTime;
        observers = new ArrayList<>();
        groupedSubmits = new HashMap<>();
        solvedProblems = new HashMap<>();
        penalties = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public long getTotalTime() {
        return totalTime;
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

    private long calculatePenalty(Set<Submit> submits, Submit solution) {
        long penalty = 0;
        for (var sub : submits)
            if (sub.getTime().isBefore(solution.getTime()))
                penalty += sub.getStatus().getPenalty();
        return penalty;
    }

    private void addOK(Submit submit, Problem problem, Set<Submit> submits) {
        solvedProblems.put(problem, submit);
        score += problem.getScore();
        long penalty = calculatePenalty(submits, submit);
        penalties.put(problem, penalty);
        totalTime += TimeUnit.MINUTES.toMillis(penalty) + ChronoUnit.MILLIS.between(startTime, submit.getTime());
    }

    private void removeOK(Submit submit, Problem problem) {
        solvedProblems.remove(problem);
        score -= problem.getScore();
        long penalty = penalties.get(problem);
        totalTime -= TimeUnit.MINUTES.toMillis(penalty) + ChronoUnit.MILLIS.between(startTime, submit.getTime());
    }

    @Override
    public void addSubmit(Submit submit) {
        if(submit.getContestant() != this)
            return;
        submit.addObserver(this);
        var problem = submit.getProblem();
        var submits = groupedSubmits.computeIfAbsent(problem, k -> new HashSet<>());
        submits.add(submit);
        var solution = solvedProblems.get(submit.getProblem());
        if (submit.getStatus().isOK() && (solution == null || submit.getTime().isBefore(solution.getTime()))) {
            if (solution != null)
                removeOK(solution, solution.getProblem());
            addOK(submit, submit.getProblem(), submits);
        }
        for (var observer : observers) {
            observer.notify(this, submit);
        }
    }

    @Override
    public void notify(Submit submit, Status oldStatus) {
        var problem = submit.getProblem();
        var submits = groupedSubmits.get(problem);
        var solution = solvedProblems.get(problem);
        if (submit.getStatus() != oldStatus && solution == submit) {
            removeOK(submit, problem);
            solution = null;
            for (var sub : submits)
                if (sub.getStatus().isOK() && (solution == null || sub.getTime().isBefore(solution.getTime())))
                    solution = sub;
            if (solution != null)
                addOK(solution, problem, submits);

        } else if (submit.getStatus().isOK() && (solution == null
                || submit.getTime().isBefore(solution.getTime()))) {
            if (solution != null)
                removeOK(solution, problem);
            addOK(submit, problem, submits);
        }
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
}
