package com.github.ruslanye.RankResolver.Controller;

import com.github.ruslanye.RankResolver.Model.Domain.Contest;
import com.github.ruslanye.RankResolver.Model.Domain.Status;
import com.github.ruslanye.RankResolver.Model.Domain.Submit;
import com.github.ruslanye.RankResolver.Model.Domain.SubmitObserver;
import com.github.ruslanye.RankResolver.Model.Graphics.LiveRanking;
import com.github.ruslanye.RankResolver.Model.Graphics.LiveResults;
import com.github.ruslanye.RankResolver.Model.Utils.Config;
import com.github.ruslanye.RankResolver.Model.Utils.ContestLoader;
import com.github.ruslanye.RankResolver.Model.Utils.Fetcher;
import javafx.fxml.FXML;

public class ContestController {

    private Config conf;
    private Contest contest;
    private Fetcher fetcher;
    private LiveResults results;
    private LiveRanking ranking;

    public void startContest(){
        conf = new Config();
        contest = ContestLoader.loadContest(conf);
        fetcher = new Fetcher(conf, contest);
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
            results = new LiveResults(conf);
            fetcher.addObserver(results);
        }
        results.show();
    }

    @FXML
    public void liveRanking(){
        if(ranking == null) {
            ranking = new LiveRanking(contest, conf);
            for(var contestant : contest.getContestants())
                contestant.addObserver(ranking);
        }
        ranking.show();
    }
}
