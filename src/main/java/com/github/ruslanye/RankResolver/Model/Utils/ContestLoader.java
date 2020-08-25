package com.github.ruslanye.RankResolver.Model.Utils;

import com.github.ruslanye.RankResolver.Model.Domain.*;
import com.opencsv.CSVReader;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class ContestLoader {
    public static List<Submit> loadSubmits(String path, Contest contest) {
        LinkedList<Submit> list = null;
        boolean success = false;
        Config conf = Config.getConfig();
        while (!success) {
            try (
                    Reader reader = Files.newBufferedReader(Paths.get(path));
                    CSVReader csvReader = new CSVReader(reader)
            ) {
                List<String[]> records = csvReader.readAll();
                list = new LinkedList<>();
                for (var record : records) {
                    var number = Long.parseLong(record[0]);
                    var contestant = contest.getContestant(record[1]);
                    var problem = contest.getProblem(record[2]);
                    var time = LocalDateTime.parse(record[3], Config.formatter);
                    if(!(time.isAfter(conf.startTime) && time.isBefore(conf.startTime.plusMinutes(conf.contestDuration))))
                        continue;
                    var status = contest.getStatus(record[4]);
                    list.add(new Submit(number, contestant, problem, time, status));
                }
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
        }
        return list;
    }

    public static List<Contestant> loadContestants(String path) {
        LinkedList<Contestant> list = null;
        boolean success = false;
        Config conf = Config.getConfig();
        while (!success) {
            try (
                    Reader reader = Files.newBufferedReader(Paths.get(path));
                    CSVReader csvReader = new CSVReader(reader)
            ) {
                List<String[]> records = csvReader.readAll();
                list = new LinkedList<>();
                for (var record : records) {
                    var name = record[0];
                    list.add(new Contestant(name, conf.startTime, conf.startTime.plusMinutes(conf.liveDuration)));
                }
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
        }
        return list;
    }

    public static List<Status> loadStatuses(String path){
        LinkedList<Status> list = null;
        boolean success = false;
        while (!success) {
            try (
                    Reader reader = Files.newBufferedReader(Paths.get(path));
                    CSVReader csvReader = new CSVReader(reader)
            ) {
                List<String[]> records = csvReader.readAll();
                list = new LinkedList<>();
                for (var record : records) {
                    var status = record[0];
                    var penalty = Integer.parseInt(record[1]);
                    list.add(new Status(status, penalty));
                }
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
        }
        return list;
    }

    public static List<Problem> loadProblems(String path){
        LinkedList<Problem> list = null;
        boolean success = false;
        while (!success) {
            try (
                    Reader reader = Files.newBufferedReader(Paths.get(path));
                    CSVReader csvReader = new CSVReader(reader)
            ) {
                List<String[]> records = csvReader.readAll();
                list = new LinkedList<>();
                for (var record : records) {
                    var id = record[0];
                    var score = Integer.parseInt(record[1]);
                    list.add(new Problem(id, score));
                }
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
        }
        return list;
    }

    public static Contest loadContest(){
        Contest contest = new Contest();
        contest.addContestants(loadContestants(Config.contestantsPath));
        contest.addProblems(loadProblems(Config.problemsPath));
        contest.addStatuses(loadStatuses(Config.statusesPath));
        return contest;
    }
}
