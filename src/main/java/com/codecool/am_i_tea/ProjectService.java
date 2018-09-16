package com.codecool.am_i_tea;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProjectService {

    private ProjectDAO projectDAO;

    public ProjectService(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    public boolean createProject(String projectName) {

        String homeFolder = System.getProperty("user.home");
        String projectPath = homeFolder + File.separator + "AmITea" + File.separator + projectName;

        File project = new File(projectPath);
        if (!project.exists()) {
            if (project.mkdirs()) {
                System.out.println("Project directory is created!");
                projectDAO.setCurrentProject(new Project(projectName, projectPath));
                return true;
            } else {
                System.out.println("Failed to create project directory!");
                return false;
            }
        } else {
            System.out.println("Project already exists. Choose another name!");
            return false;
        }
    }

    public List<String> getAllProjects() {
        String homeFolder = System.getProperty("user.home");
        String path = homeFolder + File.separator + "AmITea";
        File file = new File(path);
        String[] projects = file.list((current, name) -> new File(current, name).isDirectory());
        if (projects != null) {
            System.out.println("Found the list of all projects!");
            return new ArrayList<>(Arrays.asList(projects));
        } else {
            System.out.println("Couldn't find the list of projects!");
            return null;
        }
    }
}
