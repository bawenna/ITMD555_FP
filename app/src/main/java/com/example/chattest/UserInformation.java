package com.example.chattest;

import java.io.Serializable;
import java.util.Date;

public class UserInformation implements Serializable {

    private String name;

    public String getInterest2() {
        return interest2;
    }

    public void setInterest2(String interest2) {
        this.interest2 = interest2;
    }

    public String getInterest3() {
        return interest3;
    }

    public void setInterest3(String interest3) {
        this.interest3 = interest3;
    }

    private String email;
    private String interest;
    private String interest2;
    private String interest3;

    public UserInformation(String name, String email, String interest, String interest2, String interest3) {
        this.name = name;
        this.email = email;
        this.interest = interest;
        this.interest2 = interest2;
        this.interest3 = interest3;
    }
    public UserInformation(String name, String email, String interest, String interest2) {
        this.name = name;
        this.email = email;
        this.interest = interest;
        this.interest2 = interest2;
    }

    public UserInformation(String name, String email) {
        this.name = name;
        this.email = email;
    }

    private String schoolYear;

    public UserInformation(){

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }
}
