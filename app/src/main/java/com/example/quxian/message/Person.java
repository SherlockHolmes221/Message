package com.example.quxian.message;

/**
 * Created by quxian on 2018/7/19.
 */

public class Person {

    private String name;
    private String phoneNumber;
    private String content;

    public Person(String userName, String phoneNumber, String content) {
        this.name = userName;
        this.phoneNumber = phoneNumber;
        this.content = content;
    }

    public Person() {
    }

    public String getName() {
        return name;
    }

    public void setName(String userName) {
        this.name = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
