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

    public static String getConfigPath() {
        return System.getProperty("user.dir") + File.separator + "config";
    }

    public static String getConfigPath(String path){
        return getConfigPath() + File.separator + path;
    }

    public static boolean ensureConfigExists(){
        File f = new File(getConfigPath());
        if(!f.exists()){
            return f.mkdir();
        }
        return true;
    }

    public static Properties getDefaultProperties() {
        Properties prop = new Properties();
        prop.setProperty("startTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        prop.setProperty("duration", "120");
        prop.setProperty("location", System.getProperty("user.dir") + File.separator + "submits.csv");
        return prop;
    }

    public static boolean validate(Properties prop) {
        String startTime = prop.getProperty("startTime");
        String duration = prop.getProperty("duration");
        String location = prop.getProperty("location");
        try {
            LocalDateTime.parse(startTime, formatter);
        } catch (Exception e) {
            return false;
        }
        File f = new File(location);
        try {
            f.getCanonicalPath();
        } catch (IOException e) {
            return false;
        }
        try {
            int dur = Integer.parseInt(duration);
            if (dur <= 0)
                return false;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void saveProperties(Properties prop){
        File f = new File(propPath);
        if(!f.exists()) {
            try {
                if(!ensureConfigExists())
                    return;
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        try {
            prop.store(new FileWriter(f), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties loadProperties() {
        Properties prop = getDefaultProperties();
        try{
            prop.load(new FileInputStream((propPath)));
        } catch (FileNotFoundException e){
            saveProperties(prop);
        } catch (IOException e){
            e.printStackTrace();
        }
        return prop;
    }
}
