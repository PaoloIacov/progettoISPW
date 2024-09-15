package model.domain;

import java.io.Serializable;

public class Conversation implements Serializable {
    private Long conversationId;
    private String description;
    private String projectName;

    public Conversation() {
    }

    public Conversation(Long conversationId, String description, String projectName) {
        this.conversationId = conversationId;
        this.description = description;
        this.projectName = projectName;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
