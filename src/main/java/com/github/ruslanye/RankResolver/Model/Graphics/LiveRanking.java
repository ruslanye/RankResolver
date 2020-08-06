package com.github.ruslanye.RankResolver.Model.Graphics;

import com.github.ruslanye.RankResolver.Model.Domain.Contest;
import com.github.ruslanye.RankResolver.Model.Domain.Contestant;
import com.github.ruslanye.RankResolver.Model.Domain.ContestantObserver;
import com.github.ruslanye.RankResolver.Model.Domain.Submit;
import com.github.ruslanye.RankResolver.Model.Utils.Config;
import javafx.stage.Stage;

public class LiveRanking extends Stage implements ContestantObserver {

    private final Contest contest;
    private final Config conf;

    public LiveRanking(Contest contest, Config conf){
        this.contest = contest;
        this.conf = conf;
    }

    @Override
    public void notify(Contestant contestant, Submit submit) {

    }
}
