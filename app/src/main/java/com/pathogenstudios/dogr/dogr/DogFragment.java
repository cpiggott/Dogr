package com.pathogenstudios.dogr.dogr;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.andtinder.model.CardData;
import com.andtinder.model.DogCardData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HaydenKinney on 2/22/15.
 */

public class DogFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "6";
    ListView list;
    TextView name;
    TextView description;

    ArrayList<Dog> dogsList;
    private String selectedBio;
    private String selectedName;
    private String selectedId;
    private static CardData cardData;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static DogFragment newInstance(int sectionNumber, CardData cd) {
        DogFragment fragment = new DogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        setValues(cd);

        return fragment;
    }

    public DogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dog_profile, container, false);
//        userImage = (ImageButton) rootView.findViewById(R.id.imageButtonUpdateUser);
        list = (ListView)rootView.findViewById(R.id.listView);
        name = (TextView)rootView.findViewById(R.id.textView);
        description = (TextView)rootView.findViewById(R.id.textView2);

        selectedId = cardData.UserObjId;
        selectedBio = cardData.UserBio;
        selectedName = cardData.UserName;

        name.setText(selectedName);
        description.setText(selectedBio);


        dogsList = new ArrayList<Dog>();
        createDogs();
        setUpView();

        return rootView;

    }

    public static void setValues(CardData cd) {
        cardData = cd;
    }

    private void setUpView() {
        Dog[] dogsArray = new Dog[dogsList.size()];
        dogsArray = dogsList.toArray(dogsArray);
        DogAdapter customAdapter = new DogAdapter( getActivity(), R.layout.item_dog, dogsArray);
        list.setAdapter(customAdapter);

    }

    private void createDogs() {
        List<DogCardData> dCD = cardData.ListOfDogs;
        for(int i = 0; i < dCD.size(); i++) {
            dogsList.add(new Dog(dCD.get(i).Name, dCD.get(i).Breed1, dCD.get(i).Gender,
               dCD.get(i).DailyGoal, dCD.get(i).Weight ));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}

