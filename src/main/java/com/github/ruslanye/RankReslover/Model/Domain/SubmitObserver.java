package com.github.ruslanye.RankReslover.Model.Domain;

public interface SubmitObserver {
    void notify(Submit submit, Status oldStatus);
}
