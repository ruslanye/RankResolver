package com.github.ruslanye.RankResolver.Model.Utils;

import com.github.ruslanye.RankResolver.Model.Domain.Contest;
import com.github.ruslanye.RankResolver.Model.Domain.Submit;
import com.github.ruslanye.RankResolver.Model.Domain.SubmitObserver;

import java.util.LinkedList;
import java.util.List;

public class Fetcher implements Runnable {
    private final List<SubmitObserver> observers;
    private final Config conf;
    private final Contest contest;
    private final List<Submit> submits;

    public Fetcher(Contest con){
        conf = Config.getConfig();
        contest = con;
        observers = new LinkedList<>();
        submits = new LinkedList<>();
    }

    public void addObserver(SubmitObserver observer){
        observers.add(observer);
    }

    public void removeObserver(SubmitObserver observer){
        observers.remove(observer);
    }

    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(conf.fetcherDelay);
            } catch (InterruptedException e) {
                return;
            }
            var newSubmits = ContestLoader.loadSubmits(conf.submitsPath, contest);
            var it1 = submits.listIterator();
            var it2 = newSubmits.listIterator();
            LinkedList<Submit> toAdd = new LinkedList<>();
            while(it1.hasNext() || it2.hasNext()){
                if(it1.hasNext()){
                    var s1 = it1.next();
                    if(it2.hasNext()){
                        var s2 = it2.next();
                        if(s1.getNumber() == s2.getNumber()){
                            if(!s1.equals(s2))
                                s1.changeStatus(s2.getStatus());
                        } else{
                            //TODO: something is wrong
                        }
                    } else{
                        //TODO: something is wrong
                    }
                } else{
                    var s2 = it2.next();

                    for(var observer : observers)
                        observer.addSubmit(s2);
                    toAdd.add(s2);
                }
            }
            submits.addAll(toAdd);
        }
    }
}
