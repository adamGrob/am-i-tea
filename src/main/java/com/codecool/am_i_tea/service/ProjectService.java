package com.codecool.am_i_tea.service;

import com.codecool.am_i_tea.model.Project;
import com.codecool.am_i_tea.dao.ProjectDAO;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProjectService {

    private ProjectDAO projectDAO;
    private PropertyUtil propertyUtil;

    public ProjectService(ProjectDAO projectDAO, PropertyUtil propertyUtil) {
        this.propertyUtil = propertyUtil;
        this.projectDAO = projectDAO;
    }

    public boolean createProject(String projectName) {

        String homeFolder = propertyUtil.getLocationProperty();
        String projectPath = homeFolder + File.separator + projectName;

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

    public void loadProject(String projectName) {
        String homeFolder = propertyUtil.getLocationProperty();
        String projectPath = homeFolder + File.separator + projectName;
        File project = new File(projectPath);
        if (project.exists()){
            projectDAO.setCurrentProject(new Project(projectName, projectPath));
            System.out.println("Successfully opened project!");
        } else {
            System.out.println("Couldn't open project!");
        }
    }

    public List<String> getAllProjects() {
        String path = propertyUtil.getLocationProperty();
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
