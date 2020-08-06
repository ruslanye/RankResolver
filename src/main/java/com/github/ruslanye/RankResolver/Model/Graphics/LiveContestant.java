package com.github.ruslanye.RankResolver.Model.Graphics;

import com.github.ruslanye.RankResolver.Model.Domain.Contest;
import com.github.ruslanye.RankResolver.Model.Domain.Contestant;
import com.github.ruslanye.RankResolver.Model.Utils.Config;
import javafx.scene.layout.StackPane;

public class LiveContestant extends StackPane {

    private final Contestant contestant;
    private final Contest contest;
    private final Config conf;

    public LiveContestant(Contestant contestant, Contest contest, Config conf) {
        this.contestant = contestant;
        this.contest = contest;
        this.conf = conf;
    }
}
