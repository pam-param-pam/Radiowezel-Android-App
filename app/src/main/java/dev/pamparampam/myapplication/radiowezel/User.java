package dev.pamparampam.myapplication.radiowezel;

public class User {

    private boolean isLogged;
    private String nickname;
    private String email;
    private String first_name;
    private String second_name;
    private boolean isStaff;
    private String token;
    private static User user;
    public static User getInstance() {

        return user;
    }
    User(String nickname, String email, String first_name, String second_name, boolean isStaff, String token) {
        this.isLogged = true;
        this.nickname = nickname;
        this.email = email;
        this.first_name = first_name;
        this.second_name = second_name;
        this.isStaff = isStaff;
        this.token = token;
        user = this;

    }

    public boolean isLogged() {
        return isLogged;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getSecond_name() {
        return second_name;
    }

    public boolean isStaff() {
        return isStaff;
    }

    public String getToken() {
        return token;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setSecond_name(String second_name) {
        this.second_name = second_name;
    }

    public  void logout() {
        isStaff = false;
        isLogged = false;
        email = null;
        nickname = null;
        first_name = null;
        second_name = null;

    }
}
