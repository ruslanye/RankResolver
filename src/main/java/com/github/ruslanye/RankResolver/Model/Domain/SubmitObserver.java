package com.github.ruslanye.RankResolver.Model.Domain;

public interface SubmitObserver {
    void notify(Submit submit, Status oldStatus);
    void addSubmit(Submit submit);
}
