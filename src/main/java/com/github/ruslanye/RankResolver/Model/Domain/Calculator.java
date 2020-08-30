package com.github.ruslanye.RankResolver.Model.Domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Calculator {
    private final LocalDateTime startTime;
    private final Map<Problem, Submit> solvedProblems;
    private final Map<Problem, List<Submit>> groupedSubmits;
    private final Map<Problem, Long> times;
    private int score;
    private long totalTime;

    public Calculator(LocalDateTime startTime) {
        score = 0;
        totalTime = 0;
        this.startTime = startTime;
        solvedProblems = new HashMap<>();
        groupedSubmits = new HashMap<>();
        times = new HashMap<>();
    }

    public int getScore() {
        return score;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public List<Submit> getSubmits(Problem problem) {
        return Collections.unmodifiableList(groupedSubmits.computeIfAbsent(problem, k -> new LinkedList<>()));
    }

    public Submit getSolution(Problem problem) {
        return solvedProblems.get(problem);
    }

    public long getTime(Problem problem) {
        if (solvedProblems.get(problem) == null)
            return 0;
        return times.get(problem);
    }

    private long calculatePenalty(List<Submit> submits, Submit solution) {
        long penalty = 0;
        for (var sub : submits)
            if (sub.getTime().isBefore(solution.getTime()))
                penalty += sub.getStatus().getPenalty();
        return penalty;
    }

    private void addOK(Submit submit, Problem problem, List<Submit> submits) {
        solvedProblems.put(problem, submit);
        score += problem.getScore();
        long time = TimeUnit.MINUTES.toMillis(calculatePenalty(submits, submit)) +
                ChronoUnit.MILLIS.between(startTime, submit.getTime());
        times.put(problem, time);
        totalTime += time;
    }

    private void removeOK(Problem problem) {
        solvedProblems.remove(problem);
        score -= problem.getScore();
        totalTime -= times.get(problem);
    }

    public void addSubmit(Submit submit) {
        var problem = submit.getProblem();
        var submits = groupedSubmits.computeIfAbsent(problem, k -> new LinkedList<>());
        submits.add(submit);
        var solution = solvedProblems.get(submit.getProblem());
        if (submit.getStatus().isOK() && (solution == null || submit.getTime().isBefore(solution.getTime()))) {
            if (solution != null)
                removeOK(solution.getProblem());
            addOK(submit, submit.getProblem(), submits);
        }
    }

    public void updateSubmit(Submit submit, Status oldStatus) {
        var problem = submit.getProblem();
        var submits = groupedSubmits.get(problem);
        var solution = solvedProblems.get(problem);
        if (submit.getStatus() != oldStatus && solution == submit) {
            removeOK(problem);
            solution = null;
            for (var sub : submits)
                if (sub.getStatus().isOK() && (solution == null || sub.getTime().isBefore(solution.getTime())))
                    solution = sub;
            if (solution != null)
                addOK(solution, problem, submits);

        } else if (submit.getStatus().isOK() && (solution == null
                || submit.getTime().isBefore(solution.getTime()))) {
            if (solution != null)
                removeOK(problem);
            addOK(submit, problem, submits);
        }
    }

    public int countAttempts(Problem problem) {
        int count = 0;
        var solution = getSolution(problem);
        if (solution == null)
            return getSubmits(problem).size();
        for (var sub : getSubmits(problem))
            if (!sub.getTime().isAfter(solution.getTime()))
                count++;
        return count;
    }
}
