package com.smartlearning.dto;

/**
 * 登录请求DTO
 */
public class LoginRequest {

    private String studentId;
    private String password;

    // getter and setter
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}