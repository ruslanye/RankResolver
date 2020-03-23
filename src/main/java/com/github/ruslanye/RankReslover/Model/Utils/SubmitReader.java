package com.github.ruslanye.RankReslover.Model.Utils;

import com.github.ruslanye.RankReslover.Model.Domain.Contestant;
import com.github.ruslanye.RankReslover.Model.Domain.Problem;
import com.github.ruslanye.RankReslover.Model.Domain.Status;
import com.github.ruslanye.RankReslover.Model.Domain.Submit;
import com.opencsv.CSVReader;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SubmitReader {

    public static List<Submit> load(String path, Function<String, Contestant> getContestant,
                                    Function<String, Problem> getProblem) {
        ArrayList<Submit> list = new ArrayList<>();
        boolean success = false;
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
                    var timestamp = record[3];
                    var status = Status.valueOf(record[4]);
                    list.add(new Submit(number, contestant, problem, timestamp, status));
                }
                success = true;
            } catch (Exception e) {
                success = false;
            }
        }
        return list;
    }
}
