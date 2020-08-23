package com.github.ruslanye.RankResolver.Model.Utils;

import javafx.scene.paint.Color;

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
    
    private final Properties prop;

    public final LocalDateTime startTime;
    public final String submitsPath;
    public final int liveDuration;
    public final int contestDuration;
    public final int fetcherDelay;
    public final double liveResultsWidth;
    public final double liveResultsHeight;
    public final double liveResultsTimeout;
    public final double liveResultsMoveDuration;
    public final double liveResultsFadeDuration;
    public final double liveResultsStayDuration;
    public final int liveResultsSubmitsLimit;
    public final double rankingWidth;
    public final double rankingHeight;
    public final int rankingContestantsLimit;
    public final double boxWidth;
    public final double liveRankingMoveDuration;
    public final Color queuedColor;
    public final Color failedColor;
    public final Color solvedColor;
    public final Color solvedFirstColor;
    public final Color headerColor;
    public final Color rowColor1;
    public final Color rowColor2;
    public final double fontSize;

    public Config(){
        prop = new Properties();
        try {
            prop.load(new FileInputStream(propPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        startTime = LocalDateTime.parse(prop.getProperty("startTime"), formatter);
        submitsPath = prop.getProperty("submitsPath");
        liveDuration = getIntProp("liveDuration");
        contestDuration = getIntProp("contestDuration");
        fetcherDelay = getIntProp("fetcherDelay");
        liveResultsWidth = getDoubleProp("liveResultsWidth");
        liveResultsHeight = getDoubleProp("liveResultsHeight");
        liveResultsTimeout = getDoubleProp("liveResultsTimeout");
        liveResultsMoveDuration = getDoubleProp("liveResultsMoveDuration");
        liveResultsFadeDuration = getDoubleProp("liveResultsFadeDuration");
        liveResultsStayDuration = getDoubleProp("liveResultsStayDuration");
        liveResultsSubmitsLimit = getIntProp("liveResultsSubmitsLimit");
        rankingWidth = getDoubleProp("rankingWidth");
        rankingHeight = getDoubleProp("rankingHeight");
        rankingContestantsLimit = getIntProp("rankingContestantsLimit");
        boxWidth = getDoubleProp("boxWidth");
        liveRankingMoveDuration = getDoubleProp("liveRankingMoveDuration");
        queuedColor = getColorProp("queuedColor");
        failedColor = getColorProp("failedColor");
        solvedColor = getColorProp("solvedColor");
        solvedFirstColor = getColorProp("solvedFirstColor");
        headerColor = getColorProp("headerColor");
        rowColor1 = getColorProp("rowColor1");
        rowColor2 = getColorProp("rowColor2");
        fontSize = getDoubleProp("fontSize");
    }
    
    private double getDoubleProp(String name){
        return Double.parseDouble(prop.getProperty(name));
    }
    
    private int getIntProp(String name){
        return Integer.parseInt(prop.getProperty(name));
    }

    private Color getColorProp(String name){
        return Color.web(prop.getProperty(name));
    }
}
