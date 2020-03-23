package com.github.ruslanye.RankReslover.Model.Domain;

import java.util.ArrayList;
import java.util.List;

public class Submit {
    private int number;
    private Status status;
    private Problem problem;
    private String timestamp;
    private Contestant contestant;
    private List<SubmitObserver> observers;

    public Submit(int number, Contestant contestant, Problem problem, String timestamp, Status status){
        this.number = number;
        this.contestant = contestant;
        this.problem = problem;
        this.timestamp = timestamp;
        this.status = status;
        observers = new ArrayList<>();
    }

    public void changeStatus(Status newStatus){
        var oldStatus = status;
        status = newStatus;
        for(var observer : observers){
            observer.notify(this, oldStatus);
        }
    }

    public int getNumber(){
        return number;
    }

    public Status getStatus(){
        return status;
    }

    public String getTimestamp(){
        return timestamp;
    }

    public Problem getProblem(){
        return problem;
    }

    public Contestant getContestant(){
        return contestant;
    }

    public void addObserver(SubmitObserver observer){
        observers.add(observer);
    }

    public void removeObserver(SubmitObserver observer){
        observers.remove(observer);
    }
}
