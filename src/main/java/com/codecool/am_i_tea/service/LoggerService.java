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
    private File logFile;

    public LoggerService() {
    }

    public void initializeLogger() {

        String osName = System.getProperty("os.name");

        if (osName.contains("Linux")) {
            String homeFolder = System.getProperty("user.home");
            path = homeFolder + File.separator + ".config" + File.separator + "AmITea";
        } else if (osName.toLowerCase().contains("windows")) {
            String programData = System.getenv("APPDATA");
            path = programData + File.separator + "AmITea" + File.separator + "config";
        } else{
                System.out.println("This program is only designed to work under Windows or Linux operation systems!");
        }

        Logger amITeaLogger = Logger.getLogger("AmITeaLogger");
        String logFilePath = path + File.separator + "logs";

        LocalDateTime temp = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH-mm-ss");
        String logFileName = "log_" + temp.toLocalDate().toString() + "T" + temp.toLocalTime().format(formatter) + ".txt";
        createLogFile(logFileName);
    }

    public void log(String message) {
        Logger amITeaLogger = Logger.getLogger("AmITeaLogger");
        try {
            FileHandler logFileHandler = new FileHandler(path + File.separator + "logs" + File.separator + logFile.getName(), true);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            logFileHandler.setFormatter(simpleFormatter);
            amITeaLogger.addHandler(logFileHandler);

            amITeaLogger.info(message);
            logFileHandler.close();
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
        File logFile = new File(path + File.separator + "logs" + File.separator + fileName);
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
                    this.logFile = logFile;
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
