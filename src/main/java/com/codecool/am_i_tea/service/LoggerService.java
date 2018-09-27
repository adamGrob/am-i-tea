package com.codecool.am_i_tea.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerService {

    private String path;

    public LoggerService() {
    }

    public void initializeLogger() {

        switch (System.getProperty("os.name")) {
            case "Linux":
                String homeFolder = System.getProperty("user.home");
                path = homeFolder + File.separator + ".config" + File.separator + "AmITea";
                break;
            case "Windows":
                String programData = System.getenv("%PROGRAMDATA%");
                path = programData + File.separator + "AmITea" + File.separator + "config";
                break;
            default:
                System.out.println("This program is only designed to work under Windows or Linux operation systems!");
                break;
        }

        Logger amITeaLogger = Logger.getLogger("AmITeaLogger");
        String logFilePath = path + File.separator + "logs";

        LocalDateTime temp = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH-mm-ss");
        String logFileName = "log_" + temp.toLocalDate().toString() + "T" + temp.toLocalTime().format(formatter) + ".txt";
        createLogFile(logFileName);

        try {
            FileHandler logFileHandler = new FileHandler(logFilePath + File.separator + logFileName);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            logFileHandler.setFormatter(simpleFormatter);
            amITeaLogger.addHandler(logFileHandler);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public Logger getLogger() {
        return Logger.getLogger("AmITeaLogger");
    }

    public String getPath() {
        return path;
    }

    private void createLogFile(String fileName) {
        File logFolder = new File(path + File.separator + "logs");
        File logFile = new File(path + File.separator + fileName);
        if (!logFolder.exists()) {
            if (logFolder.mkdirs()) {
                System.out.println("Log directory created!");
            } else {
                System.out.println("Failed to create log directory!");
            }
        }
        if (!logFile.exists()) {
            try {
                if (logFile.createNewFile()) {
                    System.out.println("Log file created!");
                } else {
                    System.out.println("Failed to create log file!");
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
