package com.pathogenstudios.dogr.dogr;

/**
 * Created by HaydenKinney on 2/22/15.
 */

/**
 * Created by HaydenKinney on 2/22/15.
 */
public class Dog {
    private String name;
    private String breed;
    private String gender;
    private String dailyGoal;
    private String weight;

    public Dog(String name, String breed, String gender, String dailyGoal, String weight) {
        this.name = name;
        this.breed = breed;
        this.gender = gender;
        this.dailyGoal = dailyGoal;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public String getBreed() {
        return breed;
    }

    public String getGender() {
        return gender;
    }

    public String getDailyGoal() {
        return dailyGoal;
    }

    public String getWeight() {
        return weight;
    }
}
