package com.github.ruslanye.RankResolver.Model.Utils;

import com.github.ruslanye.RankResolver.Model.Domain.Contestant;
import com.github.ruslanye.RankResolver.Model.Domain.Problem;
import com.github.ruslanye.RankResolver.Model.Domain.Status;
import com.github.ruslanye.RankResolver.Model.Domain.Submit;
import com.opencsv.CSVReader;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SubmitReader {

    public static List<Submit> load(String path, Function<String, Contestant> getContestant,
                                    Function<String, Problem> getProblem) {
        ArrayList<Submit> list = null;
        boolean success = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        while (!success) {
            try (
                    Reader reader = Files.newBufferedReader(Paths.get(path));
                    CSVReader csvReader = new CSVReader(reader);
            ) {
                List<String[]> records = csvReader.readAll();
                list = new ArrayList<>();
                for (var record : records) {
                    int number = Integer.parseInt(record[0]);
                    var contestant = getContestant.apply(record[1]);
                    var problem = getProblem.apply(record[2]);
                    var time = LocalDateTime.parse(record[3], formatter);
                    var status = Status.valueOf(record[4]);
                    list.add(new Submit(number, contestant, problem, time, status));
                }
                success = true;
            } catch (Exception e) {
                success = false;
            }
        }
        return list;
    }
}
