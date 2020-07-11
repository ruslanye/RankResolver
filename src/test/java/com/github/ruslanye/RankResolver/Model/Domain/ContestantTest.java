package com.github.ruslanye.RankResolver.Model.Domain;

import com.github.ruslanye.RankResolver.Model.Utils.Config;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContestantTest {
    static final String START_TIME = "2020-04-28 11:30:00";
    static final int POINTS_PER_PROBLEM = 10;
    static final long PENALTY = 20;
    static LocalDateTime startTime;
    static LocalDateTime[] timestamps;
    static Problem[] problems;
    Contestant contestant;

    @BeforeAll
    public static void setup() {
        for (var status : Status.values())
            if (!(status == Status.OK || status == Status.CME || status == Status.INT || status == Status.EXT
                    || status == Status.QUE))
                status.setPenalty(PENALTY);
        problems = new Problem[26];
        timestamps = new LocalDateTime[24];
        for (int i = 0; i < 26; i++)
            problems[i] = new Problem(String.valueOf((char) ('A' + i)), POINTS_PER_PROBLEM);
        startTime = LocalDateTime.parse(START_TIME, Config.formatter);
        for (int i = 0; i < 24; i++) {
            timestamps[i] = startTime.plusMinutes((i + 1) * 5);
        }
    }

    @BeforeEach
    public void setupContestant() {
        contestant = new Contestant("PPP", startTime);
    }

    @Test
    public void testSimple() {
        Submit s = new Submit(123, contestant, problems[0], timestamps[0], Status.QUE);

        contestant.addSubmit(s);

        assertEquals(0, contestant.getScore());
        assertEquals(0, contestant.getTotalTime());

        s.changeStatus(Status.OK);

        assertEquals(POINTS_PER_PROBLEM, contestant.getScore());
        assertEquals(TimeUnit.MINUTES.toMillis(5), contestant.getTotalTime());
    }

    @Test
    public void testPenalty() {
        Submit s1 = new Submit(123, contestant, problems[0], timestamps[0], Status.QUE);
        Submit s2 = new Submit(124, contestant, problems[0], timestamps[1], Status.QUE);
        Submit s3 = new Submit(125, contestant, problems[0], timestamps[2], Status.QUE);

        contestant.addSubmit(s1);
        s1.changeStatus(Status.ANS);
        contestant.addSubmit(s2);
        s2.changeStatus(Status.TLE);
        contestant.addSubmit(s3);
        s3.changeStatus(Status.OK);

        assertEquals(POINTS_PER_PROBLEM, contestant.getScore());
        assertEquals(TimeUnit.MINUTES.toMillis(15 + 2 * PENALTY), contestant.getTotalTime());
    }

    @Test
    public void testRejudge() {
        Submit s1 = new Submit(123, contestant, problems[0], timestamps[0], Status.QUE);
        Submit s2 = new Submit(124, contestant, problems[0], timestamps[1], Status.QUE);
        Submit s3 = new Submit(125, contestant, problems[0], timestamps[2], Status.QUE);

        contestant.addSubmit(s1);
        s1.changeStatus(Status.TLE);
        contestant.addSubmit(s2);
        s2.changeStatus(Status.TLE);
        contestant.addSubmit(s3);
        s3.changeStatus(Status.TLE);

        assertEquals(0, contestant.getScore());
        assertEquals(0, contestant.getTotalTime());

        s3.changeStatus(Status.OK);

        assertEquals(POINTS_PER_PROBLEM, contestant.getScore());
        assertEquals(TimeUnit.MINUTES.toMillis(15 + 2 * PENALTY), contestant.getTotalTime());

        s1.changeStatus(Status.OK);

        assertEquals(POINTS_PER_PROBLEM, contestant.getScore());
        assertEquals(TimeUnit.MINUTES.toMillis(5), contestant.getTotalTime());
    }

    @Test
    public void testComplex() {
        Submit s0 = new Submit(1, contestant, problems[0], timestamps[0], Status.QUE);
        Submit s1 = new Submit(2, contestant, problems[1], timestamps[1], Status.QUE);
        Submit s21 = new Submit(3, contestant, problems[2], timestamps[2], Status.QUE);
        Submit s22 = new Submit(4, contestant, problems[2], timestamps[3], Status.QUE);
        Submit s30 = new Submit(5, contestant, problems[3], timestamps[4], Status.QUE);
        Submit s31 = new Submit(6, contestant, problems[3], timestamps[5], Status.QUE);
        Submit s32 = new Submit(7, contestant, problems[3], timestamps[6], Status.QUE);
        Submit s33 = new Submit(8, contestant, problems[3], timestamps[7], Status.QUE);

        contestant.addSubmit(s0);
        contestant.addSubmit(s1);
        contestant.addSubmit(s21);
        contestant.addSubmit(s22);
        contestant.addSubmit(s30);
        contestant.addSubmit(s31);
        contestant.addSubmit(s32);
        contestant.addSubmit(s33);

        s0.changeStatus(Status.OK);
        s1.changeStatus(Status.ANS);
        s21.changeStatus(Status.TLE);
        s22.changeStatus(Status.OK);
        s30.changeStatus(Status.CME);
        s31.changeStatus(Status.TLE);
        s32.changeStatus(Status.ANS);
        s33.changeStatus(Status.TLE);

        assertEquals(2 * POINTS_PER_PROBLEM, contestant.getScore());
        assertEquals(TimeUnit.MINUTES.toMillis(25 + PENALTY), contestant.getTotalTime());

        s32.changeStatus(Status.OK);

        assertEquals(3 * POINTS_PER_PROBLEM, contestant.getScore());
        assertEquals(TimeUnit.MINUTES.toMillis(60 + 2 * PENALTY), contestant.getTotalTime());
    }

}