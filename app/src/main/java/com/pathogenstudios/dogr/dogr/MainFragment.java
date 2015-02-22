package com.pathogenstudios.dogr.dogr;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andtinder.model.CardModel;
import com.andtinder.model.Orientations;
import com.andtinder.view.CardContainer;
import com.andtinder.view.SimpleCardStackAdapter;
import com.andtinder.model.CardData;
import com.andtinder.model.DogCardData;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 2/21/2015.
 */
public class MainFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    CardContainer mCardContainer;
    View rootView;
    List<CardData> cardData = new ArrayList<CardData>();
    List<DogCardData> dogData = new ArrayList<DogCardData>();
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MainFragment newInstance(int sectionNumber) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        RetrieveData();
        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }



    //WHAT THE DUCKers DOES THIS THING DO...
    private void RetrieveData(){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().get("username"));
        //query.whereEqualTo("objectId", "YJCYviNtZx");
        query.setLimit(25);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> parseData, ParseException e) {
                if (e == null) {
                    List<ParseUser> data = parseData;
                    for(ParseUser tempUser : data){//For every user returned

                        ParseRelation relation = tempUser.getRelation("dogs");
                        ParseQuery innerQuery = relation.getQuery();
                        List<ParseObject> innerList = new ArrayList<ParseObject>();
                        try {innerList = innerQuery.find();}
                        catch (ParseException ef) {
                        }

                        for (ParseObject dog : innerList) {
                            dogData.add(new DogCardData(dog.getObjectId().toString(), dog.getString("name"), dog.getString("breed1").toString(), dog.getString("dailyGoal").toString(), dog.getString("gender").toString(), dog.getString("neutered").toString(), dog.getString("weight").toString(), dog.getString("weightUnit").toString()));
                        }
                        cardData.add(new CardData(tempUser.getObjectId().toString(), tempUser.getString("username"), tempUser.getString("userBio"), Integer.toString(dogData.size()), dogData ));
                        dogData = new ArrayList<DogCardData>();

                        SwiperNoSwiping();
                    }

                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });

    }

    public void SwiperNoSwiping(){

        mCardContainer = (CardContainer) rootView.findViewById(R.id.cardView);
        mCardContainer.setOrientation(Orientations.Orientation.Ordered);
        SimpleCardStackAdapter adapter = new SimpleCardStackAdapter(getActivity());

        for(CardData datD : cardData){
            final CardModel card = new CardModel(datD.UserName,datD.UserBio, getResources().getDrawable(R.drawable.logo),datD);
            card.setOnCardDimissedListener(new CardModel.OnCardDimissedListener(){
                @Override
                public void onLike(){
                    Log.i("Swipeable Cards","I like the card " + card.getTitle());
                }
                public void onDislike(){
                    Log.i("Swipeable Cards","I dislike the card" + card.getTitle());
                }
            });

            card.setOnClickListener(new CardModel.OnClickListener() {
                @Override
                public void OnClickListener() {
                    Log.i("Swipeable Cards","I am pressing the card"  + card.getTitle());
                }
            });

            adapter.add(card);

        }
        mCardContainer.setAdapter(adapter);

//        CardModel card = new CardModel("Chris", "dddDayum", getResources().getDrawable(R.drawable.logo));









    }

}

