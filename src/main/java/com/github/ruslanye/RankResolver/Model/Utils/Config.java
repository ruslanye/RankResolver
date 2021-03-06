package com.github.ruslanye.RankResolver.Model.Utils;

import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class Config {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    public static final String propPath = getConfigPath("config.properties");
    public static final String contestPath = getConfigPath("contest.properties");
    public static final String contestantsPath = getConfigPath("contestants.csv");
    public static final String problemsPath = getConfigPath("problems.csv");
    public static final String statusesPath = getConfigPath("statuses.csv");
    public static final String winnerScreenPath = getConfigPath("winScreen.html");
    private static final Config instance = new Config();
    public final LocalDateTime startTime;
    public final String submitsPath;
    public final int liveDuration;
    public final int contestDuration;
    public final int fetcherDelay;
    public final double liveResultsWidth;
    public final double liveResultsHeight;
    public final Duration liveResultsTimeout;
    public final Duration liveResultsMoveDuration;
    public final Duration liveResultsFadeDuration;
    public final Duration liveResultsStayDuration;
    public final int liveResultsSubmitsLimit;
    public final double rankingWidth;
    public final double rankingHeight;
    public final int rankingContestantsLimit;
    public final Duration autoscrollDuration;
    public final Duration autoscrollDelay;
    public final Duration resolverStepDuration;
    public final Duration winScreenDuration;
    public final int winnersNumber;
    public final double boxWidth;
    public final Duration rankingMoveDuration;
    public final Color queuedColor;
    public final Color failedColor;
    public final Color solvedColor;
    public final Color solvedFirstColor;
    public final Color headerColor;
    public final Color rowColor1;
    public final Color rowColor2;
    public final Color selectedColor;
    public final double fontSize;
    public final String winScreen;
    private final Properties prop;
    private Config() {
        prop = new Properties();
        try {
            prop.load(new FileInputStream(propPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            prop.load(new FileInputStream(contestPath));
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
        liveResultsTimeout = getDurationProp("liveResultsTimeout");
        liveResultsMoveDuration = getDurationProp("liveResultsMoveDuration");
        liveResultsFadeDuration = getDurationProp("liveResultsFadeDuration");
        liveResultsStayDuration = getDurationProp("liveResultsStayDuration");
        liveResultsSubmitsLimit = getIntProp("liveResultsSubmitsLimit");
        rankingWidth = getDoubleProp("rankingWidth");
        rankingHeight = getDoubleProp("rankingHeight");
        rankingContestantsLimit = getIntProp("rankingContestantsLimit");
        autoscrollDuration = getDurationProp("autoscrollDuration");
        autoscrollDelay = getDurationProp("autoscrollDelay");
        resolverStepDuration = getDurationProp("resolverStepDuration");
        winScreenDuration = getDurationProp("winScreenDuration");
        winnersNumber = getIntProp("winnersNumber");
        boxWidth = getDoubleProp("boxWidth");
        rankingMoveDuration = getDurationProp("rankingMoveDuration");
        queuedColor = getColorProp("queuedColor");
        failedColor = getColorProp("failedColor");
        solvedColor = getColorProp("solvedColor");
        solvedFirstColor = getColorProp("solvedFirstColor");
        headerColor = getColorProp("headerColor");
        rowColor1 = getColorProp("rowColor1");
        rowColor2 = getColorProp("rowColor2");
        selectedColor = getColorProp("selectedColor");
        fontSize = getDoubleProp("fontSize");

        String temp;
        try {
            temp = Files.readString(Paths.get(winnerScreenPath));
        } catch (IOException e) {
            e.printStackTrace();
            temp = "<h1>Congratulations,</h1>\n<h1>@name!</h1>";
        }
        winScreen = temp;
    }

    public static String getConfigPath() {
        return System.getProperty("user.dir") + File.separator + "config";
    }

    public static String getConfigPath(String path) {
        return getConfigPath() + File.separator + path;
    }

    public static Config getConfig() {
        return instance;
    }

    private double getDoubleProp(String name) {
        return Double.parseDouble(prop.getProperty(name));
    }

    private int getIntProp(String name) {
        return Integer.parseInt(prop.getProperty(name));
    }

    private Color getColorProp(String name) {
        return Color.web(prop.getProperty(name));
    }

    private Duration getDurationProp(String name) {
        return Duration.millis(getDoubleProp(name));
    }
}
