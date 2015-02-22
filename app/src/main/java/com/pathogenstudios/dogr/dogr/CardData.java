package com.pathogenstudios.dogr.dogr;

import java.util.List;

/**
 * Created by Chris on 2/22/2015.
 */
public class CardData {

    public String UserObjId;
    public String UserName;
    public String UserBio;
    public String UserDogCount;
    public List<DogCardData> ListOfDogs;

    public CardData(String objId, String userName, String userBio, String userDogCount, List<DogCardData> listOfDogs){
        this.UserObjId = objId;
        this.UserName = userName;
        this.UserBio = userBio;
        this.UserDogCount = userDogCount;
        this.ListOfDogs = listOfDogs;
    }



}
