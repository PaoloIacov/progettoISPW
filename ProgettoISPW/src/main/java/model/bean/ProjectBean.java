package model.bean;

import java.io.Serializable;

// Classe ProjectBean per trasferimento dati (DTO)
public class ProjectBean implements Serializable {
    private String projectName;
    private String projectDescription;

    public ProjectBean() {}


    public ProjectBean(String projectName, String projectDescription) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
    }

    // Getter e Setter
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }
}
