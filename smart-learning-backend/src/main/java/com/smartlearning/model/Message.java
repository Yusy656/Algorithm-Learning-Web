package com.smartlearning.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 消息实体类
 */
@Entity
@Table(name = "message")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role", nullable = false, length = 20)
    private String role; // user, assistant

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "image", columnDefinition = "TEXT")
    private String image; // 存储base64编码的图片数据

    @Column(name = "timestamp", nullable = false)
    private Date timestamp;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    // getter and setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = new Date();
        }
    }
}