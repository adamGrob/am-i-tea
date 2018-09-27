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
    private LoggerService logger;

    public ProjectService(ProjectDAO projectDAO, PropertyUtil propertyUtil, LoggerService loggerService) {
        this.propertyUtil = propertyUtil;
        this.projectDAO = projectDAO;
        this.logger = loggerService;
    }

    public boolean createProject(String projectName) {

        String homeFolder = propertyUtil.getLocationProperty();
        String projectPath = homeFolder + File.separator + projectName;

        File project = new File(projectPath);
        if (!project.exists()) {
            if (project.mkdirs()) {
                logger.getLogger().info(project.getName() + " project directory created successfully!");
                projectDAO.setCurrentProject(new Project(projectName, projectPath));
                return true;
            } else {
                logger.getLogger().warning("Failed to create " + project.getName() + " project directory!");
                return false;
            }
        } else {
            logger.getLogger().warning(project.getName() + " project already exists. Choose another name!");
            return false;
        }
    }

    public void loadProject(String projectName) {
        String homeFolder = propertyUtil.getLocationProperty();
        String projectPath = homeFolder + File.separator + projectName;
        File project = new File(projectPath);
        if (project.exists()){
            projectDAO.setCurrentProject(new Project(projectName, projectPath));
            logger.getLogger().info("Successfully opened " + project.getName() + " project!");
        } else {
            logger.getLogger().warning("Couldn't open " + project.getName() + " project!");
        }
    }

    public List<String> getAllProjects() {
        String path = propertyUtil.getLocationProperty();
        File file = new File(path);
        String[] projects = file.list((current, name) -> new File(current, name).isDirectory());
        if (projects != null) {
            logger.getLogger().info("Found the list of all projects!");
            return new ArrayList<>(Arrays.asList(projects));
        } else {
            logger.getLogger().warning("Couldn't find the list of projects!");
            return null;
        }
    }
}
