package com.codecool.am_i_tea.service;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class PropertyUtil {

    private Properties properties;

    public PropertyUtil(Properties properties) {
        this.properties = properties;
    }

    public void initializeProperties() {
        switch (System.getProperty("os.name")) {
            case "Linux":
                initializeLinux();
                break;
            case "Windows":
                initializeWindows();
                break;
            default:
                System.out.println("This program is only designed to work under Windows or Linux operation systems!");
                break;
        }
    }

    private void initializeWindows() {
    }

    private void initializeLinux() {
        String homeFolder = System.getProperty("user.home");
        String path = homeFolder + File.separator + ".config" + File.separator + "AmITea";
        String fileName = "config.properties";
        File configFolder = new File(path);
        File configFile = new File(path + File.separator + fileName);
        if (!configFolder.exists()) {
            if (configFolder.mkdirs()) {
                System.out.println("Config directory created!");
            } else {
                System.out.println("Failed to create config directory!");
            }
        }
        if (!configFile.exists()) {
            try {
                if (configFile.createNewFile()) {
                    System.out.println("Config file created!");
                    //todo initialiteConfigFileProperties();
                } else {
                    System.out.println("Failed to create config file!");
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        //todo readConfigProperties();
    }
}
