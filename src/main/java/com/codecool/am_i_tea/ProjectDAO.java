package com.codecool.am_i_tea;

public class ProjectDAO {

    private Project currentProject;

    public Project getCurrentProject(){
        return currentProject;
    }

    public void setCurrentProject(Project currentProject) {
        this.currentProject = currentProject;
    }
}
