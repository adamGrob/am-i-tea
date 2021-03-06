package com.codecool.am_i_tea.service;

import com.codecool.am_i_tea.ApplicationProperties;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class PropertyUtil {

    private Properties properties;

    private String fileName;
    private LoggerService logger;
    private ApplicationProperties applicationProperties;

    public PropertyUtil(Properties properties, LoggerService loggerService,
                        ApplicationProperties applicationProperties) {
        this.properties = properties;
        this.logger = loggerService;
        this.applicationProperties = applicationProperties;
    }

    public void initializeProperties() {
        switch (System.getProperty("os.name")) {
            case "Linux":
                fileName = "config.properties";
                break;
            case "Windows":
                fileName = "config.txt";
                break;
            default:
                logger.getLogger().warning("This program is only designed to work under Windows or Linux operation systems!");
                System.out.println("This program is only designed to work under Windows or Linux operation systems!");
                break;
        }
        createConfigFile();
        readConfigProperties();
    }

    public void setLocationProperty(String location) {
        properties.setProperty("path", location);
        writeConfigProperties();
    }

    public String getLocationProperty() {
        return properties.getProperty("path");
    }

    private void readConfigProperties() {
        try {
            FileReader reader = new FileReader(applicationProperties.getConfigFolderPath()
                    + File.separator + fileName);
            properties.load(reader);
            logger.log("Properies loaded successfully!");
        } catch (IOException ex) {
            logger.log("Failed to load properties!");
            logger.log(ex.getMessage());
        }
    }

    private void writeConfigProperties() {
        try {
            FileWriter writer = new FileWriter(applicationProperties.getConfigFolderPath()
                    + File.separator + fileName);
            properties.store(writer, null);
        } catch (IOException ex) {
            logger.log(ex.getMessage());
        }
    }

    private void createConfigFile() {
        File configFolder = new File(applicationProperties.getConfigFolderPath());
        File configFile = new File(applicationProperties.getConfigFolderPath()
                + File.separator + fileName);
        if (!configFolder.exists()) {
            if (configFolder.mkdirs()) {
                logger.log("Config directory created!");
            } else {
                logger.log("Failed to create config directory!");
            }
        }
        if (!configFile.exists()) {
            try {
                if (configFile.createNewFile()) {
                    logger.log("Config file created!");
                    initializeConfigFileProperties();
                } else {
                    logger.log("Failed to create config file!");
                }
            } catch (IOException ex) {
                logger.log(ex.getMessage());
            }
        }
    }

    private void initializeConfigFileProperties() {
        String homeFolder = System.getProperty("user.home");
        String defaultPath = homeFolder + File.separator + "AmITea";
        properties.setProperty("path", defaultPath);
        writeConfigProperties();
    }
}
