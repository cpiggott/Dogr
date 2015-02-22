package com.pathogenstudios.fitbark;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

public class Dog {
    public enum Gender {
        Male,
        Female,
        Unknown
    }

    private int id;
    private String name;
    private Date birthday;
    private String breed1;
    private String breed2;
    private Gender gender;
    private int weight;
    private String weightUnits;
    private String country;
    private String zipCode;
    private boolean isNutered;
    private int dailyGoal;

    public Dog(JSONObject json) throws JSONException {
        id = json.getInt("id");
        name = json.getString("name");
        try { birthday = ApiUtils.StringToDate(json.optString("birth")); }
        catch (ParseException ex) { birthday = new Date(); }
        breed1 = getBreedFromJson(json, "breed1");
        breed2 = getBreedFromJson(json, "breed2");

        gender = Gender.Unknown;
        String genderString = json.optString("gender");
        if (genderString.equals("M")) {
            gender = Gender.Male;
        } else if (genderString.equals("F")) {
            gender = Gender.Female;
        }

        weight = json.optInt("weight");
        weightUnits = json.optString("weight_unit");
        country = json.optString("country");
        zipCode = json.optString("zip");
        isNutered = json.optBoolean("neutered");
        dailyGoal = json.optInt("daily_goal");
    }

    private String getBreedFromJson(JSONObject json, String key) throws JSONException {
        json = json.optJSONObject(key);

        if (json == null || !json.has("name")) {
            return null;
        }

        return json.getString("name");
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Date getBirthday() { return birthday; }
    public String getBreed1() { return breed1; }
    public String getBreed2() { return breed2; }
    public Gender getGender() { return gender; }
    public int getWeight() { return weight; }
    public String getWeightUnits() { return weightUnits; }
    public String getCountry() { return country; }
    public String getZipCode() { return zipCode; }
    public boolean getIsNutered() { return isNutered; }
    public int getDailyGoal() { return dailyGoal; }

    public String getBreed() {
        if (breed1 == null && breed2 == null) {
            return "Mutt";
        } else if (breed2 == null) {
            return breed1;
        } else if (breed1 == null) {
            return breed2;
        } else {
            return breed1 + " " + breed2;
        }
    }

    public String getGenderString() {
        if (gender == Gender.Male) {
            return "M";
        } else if (gender == Gender.Female) {
            return "F";
        } else {
            return "?";
        }
    }
}
