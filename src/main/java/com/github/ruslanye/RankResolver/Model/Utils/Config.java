package com.github.ruslanye.RankResolver.Model.Utils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class Config {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    public static final String propPath = getConfigPath("config.properties");
    public static final String contestantsPath = getConfigPath("contestants.csv");
    public static final String problemsPath = getConfigPath("problems.csv");
    public static final String statusesPath = getConfigPath("statuses.csv");

    public static String getConfigPath() {
        return System.getProperty("user.dir") + File.separator + "config";
    }

    public static String getConfigPath(String path){
        return getConfigPath() + File.separator + path;
    }

    public final int duration;
    public final LocalDateTime startTime;
    public final String location;
    public final int liveDuration;
    public final int fetcherDelay;
    public final double liveResultsWidth;
    public final double liveResultsHeight;
    public final double liveResultsTimeout;
    public final double submitWidth;
    public final double submitHeight;
    public final int submitsLimit;

    public Config(){
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(propPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        duration = Integer.parseInt(prop.getProperty("duration"));
        startTime = LocalDateTime.parse(prop.getProperty("startTime"), formatter);
        location = prop.getProperty("location");
        liveDuration = Integer.parseInt(prop.getProperty("liveDuration"));
        fetcherDelay = Integer.parseInt(prop.getProperty("fetcherDelay"));
        liveResultsWidth = Double.parseDouble(prop.getProperty("liveResultsWidth"));
        liveResultsHeight = Double.parseDouble(prop.getProperty("liveResultsHeight"));
        liveResultsTimeout = Double.parseDouble(prop.getProperty("liveResultsTimeout"));
        submitWidth = Double.parseDouble(prop.getProperty("submitWidth"));
        submitHeight = Double.parseDouble(prop.getProperty("submitHeight"));
        submitsLimit = Integer.parseInt(prop.getProperty("submitsLimit"));
    }
}
