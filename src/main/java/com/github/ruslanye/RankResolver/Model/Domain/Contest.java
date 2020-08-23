package com.github.ruslanye.RankResolver.Model.Domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Contest {
    private final Map<String, Contestant> contestants;
    private final Map<String, Problem> problems;
    private final Map<String, Status> statuses;

    public Contest() {
        contestants = new HashMap<>();
        problems = new HashMap<>();
        statuses = new HashMap<>();
    }

    public Contestant getContestant(String id) {
        return contestants.get(id);
    }

    public List<Contestant> getContestants() {
        return new ArrayList<>(contestants.values());
    }

    public void addContestant(Contestant contestant) {
        contestants.put(contestant.getName(), contestant);
    }

    public void addContestants(List<Contestant> contestants) {
        contestants.forEach(this::addContestant);
    }

    public Problem getProblem(String id) {
        return problems.get(id);
    }

    public List<Problem> getProblems() {
        return new ArrayList<>(problems.values());
    }

    public void addProblem(Problem problem) {
        problems.put(problem.getId(), problem);
    }

    public void addProblems(List<Problem> problems) {
        problems.forEach(this::addProblem);
    }

    public Status getStatus(String id) {
        return statuses.get(id);
    }

    public List<Status> getStatuses() {
        return new ArrayList<>(statuses.values());
    }

    public void addStatus(Status status) {
        statuses.put(status.getId(), status);
    }

    public void addStatuses(List<Status> statuses) {
        statuses.forEach(this::addStatus);
    }

    public static void rank(List<Contestant> contestants){
        contestants.sort((x, y) -> {
            var scoreComp = Long.compare(y.getScore(), x.getScore());
            var timeComp = Long.compare(x.getTime(),
                    y.getTime());
            return scoreComp == 0 ? timeComp == 0 ? x.getName().compareTo(y.getName()) : timeComp : scoreComp;
        });
    }

    public static void frozenRank(List<Contestant> contestants){
        contestants.sort((x, y) -> {
            var scoreComp = Long.compare(y.getFrozenScore(), x.getFrozenScore());
            var timeComp = Long.compare(x.getFrozenTime(),
                    y.getFrozenTime());
            return scoreComp == 0 ? timeComp == 0 ? x.getName().compareTo(y.getName()) : timeComp : scoreComp;
        });
    }
}
