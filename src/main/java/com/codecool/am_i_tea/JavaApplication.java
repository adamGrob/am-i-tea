package com.codecool.am_i_tea;

import com.codecool.am_i_tea.dao.ProjectDAO;
import com.codecool.am_i_tea.dao.TextFileDAO;
import com.codecool.am_i_tea.service.TextFileService;
import javafx.scene.web.HTMLEditor;

public class JavaApplication {

    private TextFileDAO fileDAO;
    private TextFileService fileService;
    private ProjectDAO projectDAO;
    private HTMLEditor editor;

    public JavaApplication(TextFileDAO fileDAO, TextFileService fileService, ProjectDAO projectDAO,
                           HTMLEditor editor) {
        this.fileDAO = fileDAO;
        this.fileService = fileService;
        this.projectDAO = projectDAO;
        this.editor = editor;
    }

    public void openLinkedFile(String fileName) {
        fileService.saveTextFile(projectDAO.getCurrentProject().getPath(),
                fileDAO.getCurrentFile().getName(),
                editor);
        fileService.openTextFile(fileName, projectDAO.getCurrentProject().getPath(), editor);
    }
}
