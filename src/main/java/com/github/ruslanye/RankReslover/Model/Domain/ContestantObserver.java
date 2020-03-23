package com.github.ruslanye.RankReslover.Model.Domain;

public interface ContestantObserver {
     void notify(Contestant contestant, Submit submit);
}
