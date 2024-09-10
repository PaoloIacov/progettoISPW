package model.bean;

import java.io.Serializable;

public class MessageBean implements Serializable {
    private String senderUsername;
    private String content;

    public MessageBean(String senderUsername, String content) {
        this.senderUsername = senderUsername;
        this.content = content;
    }

    // Getter e Setter
    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
