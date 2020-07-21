package com.github.ruslanye.RankResolver.Model.Utils;

import com.github.ruslanye.RankResolver.Model.Domain.*;
import com.opencsv.CSVReader;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ContestReader {

    public static List<Submit> loadSubmits(String path, Contest contest) {
        ArrayList<Submit> list = null;
        boolean success = false;
        while (!success) {
            try (
                    Reader reader = Files.newBufferedReader(Paths.get(path));
                    CSVReader csvReader = new CSVReader(reader)
            ) {
                List<String[]> records = csvReader.readAll();
                list = new ArrayList<>();
                for (var record : records) {
                    var number = Integer.parseInt(record[0]);
                    var contestant = contest.getContestant(record[1]);
                    var problem = contest.getProblem(record[2]);
                    var time = LocalDateTime.parse(record[3], Config.formatter);
                    var status = contest.getStatus(record[4]);
                    list.add(new Submit(number, contestant, problem, time, status));
                }
                success = true;
            } catch (Exception e) {
                success = false;
            }
        }
        return list;
    }

    public static List<Contestant> loadContestants(String path) {
        ArrayList<Contestant> list = null;
        boolean success = false;
        while (!success) {
            try (
                    Reader reader = Files.newBufferedReader(Paths.get(path));
                    CSVReader csvReader = new CSVReader(reader)
            ) {
                List<String[]> records = csvReader.readAll();
                list = new ArrayList<>();
                for (var record : records) {
                    var name = record[0];
                    list.add(new Contestant(name, LocalDateTime.now()));
                }
                success = true;
            } catch (Exception e) {
                success = false;
            }
        }
        return list;
    }

    public static List<Status> loadStatuses(String path){
        ArrayList<Status> list = null;
        boolean success = false;
        while (!success) {
            try (
                    Reader reader = Files.newBufferedReader(Paths.get(path));
                    CSVReader csvReader = new CSVReader(reader)
            ) {
                List<String[]> records = csvReader.readAll();
                list = new ArrayList<>();
                for (var record : records) {
                    var status = record[0];
                    var penalty = Integer.parseInt(record[1]);
                    list.add(new Status(status, penalty));
                }
                success = true;
            } catch (Exception e) {
                success = false;
            }
        }
        return list;
    }

    public static List<Problem> loadProblems(String path){
        ArrayList<Problem> list = null;
        boolean success = false;
        while (!success) {
            try (
                    Reader reader = Files.newBufferedReader(Paths.get(path));
                    CSVReader csvReader = new CSVReader(reader)
            ) {
                List<String[]> records = csvReader.readAll();
                list = new ArrayList<>();
                for (var record : records) {
                    var id = record[0];
                    var score = Integer.parseInt(record[1]);
                    list.add(new Problem(id, score));
                }
                success = true;
            } catch (Exception e) {
                success = false;
            }
        }
        return list;
    }

    public static Contest loadContest(String path){
        Contest contest = new Contest();
        contest.addContestants(loadContestants(path));
        contest.addProblems(loadProblems(path));
        contest.addStatuses(loadStatuses(path));
        return contest;
    }
}
