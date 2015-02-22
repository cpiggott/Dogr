package com.andtinder.model;

/**
 * Created by Chris on 2/22/2015.
 */
public class DogCardData {

    public String ObjIdDog;
    public String Name;
    public String Breed1;
    public String Breed2;
    public String DailyGoal;
    public String Gender;
    public String Neutered;
    public String OwnerObjId;
    public String Weight;
    public String WeightUnit;

    public DogCardData(String objId, String name, String breed1, String dailyGoal, String gender, String neutered, String weight, String weightUnit){
        this.ObjIdDog = objId;
        this.Name = name;
        this.Breed1 = breed1;
        this.DailyGoal = dailyGoal;
        this.Gender = gender;
        this.Neutered = neutered;
        this.Weight = weight;
        this.WeightUnit = weightUnit;

    }

}
