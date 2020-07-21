package com.github.ruslanye.RankResolver.Model.Domain;

import com.github.ruslanye.RankResolver.Model.Utils.Config;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContestantTest {
    static final String START_TIME = "2020-04-28 11:30:00";
    static final int POINTS_PER_PROBLEM = 10;
    static final long PENALTY = 20;
    static LocalDateTime startTime;
    static LocalDateTime[] timestamps;
    static Problem[] problems;
    static Map<String, Status> statuses;
    Contestant contestant;

    @BeforeAll
    public static void setup() {
        statuses = new HashMap<>();
        statuses.put("OK", new Status("OK", 0));
        statuses.put("CME", new Status("CME", 0));
        statuses.put("QUE", new Status("QUE", 0));
        statuses.put("ANS", new Status("ANS", PENALTY));
        statuses.put("TLE", new Status("TLE", PENALTY));
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
        Submit s = new Submit(123, contestant, problems[0], timestamps[0], statuses.get("QUE"));

        contestant.addSubmit(s);

        assertEquals(0, contestant.getScore());
        assertEquals(0, contestant.getTotalTime());

        s.changeStatus(statuses.get("OK"));

        assertEquals(POINTS_PER_PROBLEM, contestant.getScore());
        assertEquals(TimeUnit.MINUTES.toMillis(5), contestant.getTotalTime());
    }

    @Test
    public void testPenalty() {
        Submit s1 = new Submit(123, contestant, problems[0], timestamps[0], statuses.get("QUE"));
        Submit s2 = new Submit(124, contestant, problems[0], timestamps[1], statuses.get("QUE"));
        Submit s3 = new Submit(125, contestant, problems[0], timestamps[2], statuses.get("QUE"));

        contestant.addSubmit(s1);
        s1.changeStatus(statuses.get("ANS"));
        contestant.addSubmit(s2);
        s2.changeStatus(statuses.get("TLE"));
        contestant.addSubmit(s3);
        s3.changeStatus(statuses.get("OK"));

        assertEquals(POINTS_PER_PROBLEM, contestant.getScore());
        assertEquals(TimeUnit.MINUTES.toMillis(15 + 2 * PENALTY), contestant.getTotalTime());
    }

    @Test
    public void testRejudge() {
        Submit s1 = new Submit(123, contestant, problems[0], timestamps[0], statuses.get("QUE"));
        Submit s2 = new Submit(124, contestant, problems[0], timestamps[1], statuses.get("QUE"));
        Submit s3 = new Submit(125, contestant, problems[0], timestamps[2], statuses.get("QUE"));

        contestant.addSubmit(s1);
        s1.changeStatus(statuses.get("TLE"));
        contestant.addSubmit(s2);
        s2.changeStatus(statuses.get("TLE"));
        contestant.addSubmit(s3);
        s3.changeStatus(statuses.get("TLE"));

        assertEquals(0, contestant.getScore());
        assertEquals(0, contestant.getTotalTime());

        s3.changeStatus(statuses.get("OK"));

        assertEquals(POINTS_PER_PROBLEM, contestant.getScore());
        assertEquals(TimeUnit.MINUTES.toMillis(15 + 2 * PENALTY), contestant.getTotalTime());

        s1.changeStatus(statuses.get("OK"));

        assertEquals(POINTS_PER_PROBLEM, contestant.getScore());
        assertEquals(TimeUnit.MINUTES.toMillis(5), contestant.getTotalTime());
    }

    @Test
    public void testComplex() {
        Submit s0 = new Submit(1, contestant, problems[0], timestamps[0], statuses.get("QUE"));
        Submit s1 = new Submit(2, contestant, problems[1], timestamps[1], statuses.get("QUE"));
        Submit s21 = new Submit(3, contestant, problems[2], timestamps[2], statuses.get("QUE"));
        Submit s22 = new Submit(4, contestant, problems[2], timestamps[3], statuses.get("QUE"));
        Submit s30 = new Submit(5, contestant, problems[3], timestamps[4], statuses.get("QUE"));
        Submit s31 = new Submit(6, contestant, problems[3], timestamps[5], statuses.get("QUE"));
        Submit s32 = new Submit(7, contestant, problems[3], timestamps[6], statuses.get("QUE"));
        Submit s33 = new Submit(8, contestant, problems[3], timestamps[7], statuses.get("QUE"));

        contestant.addSubmit(s0);
        contestant.addSubmit(s1);
        contestant.addSubmit(s21);
        contestant.addSubmit(s22);
        contestant.addSubmit(s30);
        contestant.addSubmit(s31);
        contestant.addSubmit(s32);
        contestant.addSubmit(s33);

        s0.changeStatus(statuses.get("OK"));
        s1.changeStatus(statuses.get("ANS"));
        s21.changeStatus(statuses.get("TLE"));
        s22.changeStatus(statuses.get("OK"));
        s30.changeStatus(statuses.get("CME"));
        s31.changeStatus(statuses.get("TLE"));
        s32.changeStatus(statuses.get("ANS"));
        s33.changeStatus(statuses.get("TLE"));

        assertEquals(2 * POINTS_PER_PROBLEM, contestant.getScore());
        assertEquals(TimeUnit.MINUTES.toMillis(25 + PENALTY), contestant.getTotalTime());

        s32.changeStatus(statuses.get("OK"));

        assertEquals(3 * POINTS_PER_PROBLEM, contestant.getScore());
        assertEquals(TimeUnit.MINUTES.toMillis(60 + 2 * PENALTY), contestant.getTotalTime());
    }

}