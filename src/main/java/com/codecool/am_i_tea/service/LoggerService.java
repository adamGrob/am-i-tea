package com.codecool.am_i_tea.service;

import com.codecool.am_i_tea.ApplicationProperties;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerService {

    private File logFile;
    private ApplicationProperties applicationProperties;

    public LoggerService(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public void initializeLogger() {
        LocalDateTime temp = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH-mm-ss");
        String logFileName = "log_" + temp.toLocalDate().toString() + "T" + temp.toLocalTime().format(formatter) + ".txt";
        createLogFile(logFileName);
    }

    public void log(String message) {
        Logger amITeaLogger = Logger.getLogger("AmITeaLogger");
        try {
            FileHandler logFileHandler = new FileHandler(applicationProperties.getConfigFolderPath()
                    + File.separator + "logs" + File.separator + logFile.getName(), true);
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

    private void createLogFile(String fileName) {
        File logFolder = new File(applicationProperties.getConfigFolderPath()
                + File.separator + "logs");
        File logFile = new File(applicationProperties.getConfigFolderPath()
                + File.separator + "logs" + File.separator + fileName);
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
