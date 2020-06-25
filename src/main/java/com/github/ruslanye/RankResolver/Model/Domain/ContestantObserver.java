package com.github.ruslanye.RankResolver.Model.Domain;

public interface ContestantObserver {
     void notify(Contestant contestant, Submit submit);
}
