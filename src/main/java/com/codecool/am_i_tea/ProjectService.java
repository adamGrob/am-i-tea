package com.codecool.am_i_tea;

import java.io.File;

public class ProjectService {

    public void createProject(String projectName) {

        String homeFolder = System.getProperty("user.home");
        String projectPath = homeFolder + "/AmITea/" + projectName;

        File project = new File(projectPath);
        if (!project.exists()) {
            if (project.mkdirs()) {
                System.out.println("Project directory is created!");
            } else {
                System.out.println("Failed to create project directory!");
            }
        } else {
            System.out.println("Project already exists. Choose another name!");
        }
    }
}
