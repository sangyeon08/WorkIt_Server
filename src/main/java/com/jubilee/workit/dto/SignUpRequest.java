package com.jubilee.workit.dto;

public class SignUpRequest {
    private String email;
    private String password;
    private String passwordConfirm;
    private String loginType;  //그냥 이메일, 간편은 구글, 애플

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPasswordConfirm() { return passwordConfirm; }
    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getLoginType() { return loginType; }
    public void setLoginType(String loginType) { this.loginType = loginType; }
}