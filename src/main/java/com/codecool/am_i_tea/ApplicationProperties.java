package com.codecool.am_i_tea;

import java.io.File;

public class ApplicationProperties {

    private String configFolderPath;

    public ApplicationProperties() {
    }

    public String getConfigFolderPath() {
        return configFolderPath;
    }

    public void initialize() {
        String osName = System.getProperty("os.name");

        if (osName.contains("Linux")) {
            String homeFolder = System.getProperty("user.home");
            configFolderPath = homeFolder + File.separator + ".config" + File.separator + "AmITea";
        } else if (osName.toLowerCase().contains("windows")) {
            String programData = System.getenv("APPDATA");
            configFolderPath = programData + File.separator + "AmITea" + File.separator + "config";
        } else {
            System.out.println("This program is only designed to work under Windows or Linux operation systems!");
        }
    }
}
