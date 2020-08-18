package com.github.ruslanye.RankResolver.Model.Domain;

import java.util.*;

public class Contest implements SubmitObserver{
    private final Map<String, Contestant> contestants;
    private final Map<String, Problem> problems;
    private final Map<String, Status> statuses;
    private final List<Submit> submits;

    public Contest(){
        contestants = new HashMap<>();
        problems = new HashMap<>();
        statuses = new HashMap<>();
        submits = new ArrayList<>();
    }

    public Contestant getContestant(String id){
        return contestants.get(id);
    }

    public List<Contestant> getContestants(){
        return new LinkedList<>(contestants.values());
    }

    public void addContestant(Contestant contestant){
        contestants.put(contestant.getName(), contestant);
    }

    public void addContestants(List<Contestant> contestants){
        contestants.forEach(this::addContestant);
    }

    public Problem getProblem(String id){
        return problems.get(id);
    }

    public List<Problem> getProblems(){
        return new LinkedList<>(problems.values());
    }

    public void addProblem(Problem problem){
        problems.put(problem.getId(), problem);
    }

    public void addProblems(List<Problem> problems){
        problems.forEach(this::addProblem);
    }

    public Status getStatus(String id){
        return statuses.get(id);
    }

    public List<Status> getStatuses(){
        return new LinkedList<>(statuses.values());
    }

    public void addStatus(Status status){
        statuses.put(status.getId(), status);
    }

    public void addStatuses(List<Status> statuses){
        statuses.forEach(this::addStatus);
    }

    @Override
    public void notify(Submit submit, Status oldStatus) {

    }

    @Override
    public void addSubmit(Submit submit) {
        submits.add(submit);
    }

    public List<Submit> getSubmits(){
        return submits;
    }
}
