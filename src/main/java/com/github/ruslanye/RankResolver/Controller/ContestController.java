package com.github.ruslanye.RankResolver.Controller;

import com.github.ruslanye.RankResolver.Model.Domain.Contest;
import com.github.ruslanye.RankResolver.Model.Domain.Status;
import com.github.ruslanye.RankResolver.Model.Domain.Submit;
import com.github.ruslanye.RankResolver.Model.Domain.SubmitObserver;
import com.github.ruslanye.RankResolver.Model.Graphics.LiveRanking;
import com.github.ruslanye.RankResolver.Model.Graphics.LiveResults;
import com.github.ruslanye.RankResolver.Model.Graphics.Resolver;
import com.github.ruslanye.RankResolver.Model.Utils.ContestLoader;
import com.github.ruslanye.RankResolver.Model.Utils.Fetcher;
import javafx.fxml.FXML;

public class ContestController {
    private Contest contest;
    private Fetcher fetcher;
    private LiveResults results;
    private LiveRanking ranking;
    private Resolver resolver;

    public void startContest(){
        contest = ContestLoader.loadContest();
        fetcher = new Fetcher(contest);
        for(var contestant : contest.getContestants())
            fetcher.addObserver(contestant);
        fetcher.addObserver(new SubmitObserver() {
            @Override
            public void notify(Submit submit, Status oldStatus) {
                System.out.println("Submit #" + submit.getNumber() + " new status " + submit.getStatus().getId());
            }

            @Override
            public void addSubmit(Submit submit) {
                submit.addObserver(this);
                System.out.println("New submit #" + submit.getNumber()+ " from " + submit.getContestant().getName());
            }
        });
        Thread t = new Thread(fetcher);
        t.setDaemon(true);
        t.start();
    }

    @FXML
    public void liveResults(){
        if(results == null) {
            results = new LiveResults();
            fetcher.addObserver(results);
        }
        results.show();
    }

    @FXML
    public void liveRanking(){
        if(ranking == null) {
            ranking = new LiveRanking(contest);
            for(var contestant : contest.getContestants())
                contestant.addObserver(ranking);
            ranking.setOnCloseRequest((e -> {
                for(var contestant : contest.getContestants())
                    contestant.removeObserver(ranking);
                ranking = null;
            }));
            ranking.show();
        }
    }

    @FXML
    public void resolver(){
        if(resolver == null) {
            resolver = new Resolver(contest);
            resolver.setOnCloseRequest((e) -> resolver = null);
            resolver.show();
        }
    }
}
