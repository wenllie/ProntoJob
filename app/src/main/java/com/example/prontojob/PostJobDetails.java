package com.example.prontojob;

public class PostJobDetails {

    public String jobCategory, jobTitle, jobType, jobAddress, minSalary, maxSalary, jobDescription;

    public PostJobDetails () {}

    public PostJobDetails(String jobCategory, String jobTitle, String jobType, String jobAddress, String minSalary, String maxSalary, String jobDescription) {
        this.jobCategory = jobCategory;
        this.jobTitle = jobTitle;
        this.jobType = jobType;
        this.jobAddress = jobAddress;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.jobDescription = jobDescription;
    }
}

