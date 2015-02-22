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

/**
 * Created by Chris on 2/21/2015.
 */
public class MainFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    CardContainer mCardContainer;
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
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mCardContainer = (CardContainer) rootView.findViewById(R.id.cardView);
        mCardContainer.setOrientation(Orientations.Orientation.Ordered);



        CardModel card = new CardModel("Chris", "dddDayum", getResources().getDrawable(R.drawable.logo));

        card.setOnCardDimissedListener(new CardModel.OnCardDimissedListener(){
            @Override
            public void onLike(){
                Log.i("Swipeable Cards","I like the card");
            }
            public void onDislike(){
                Log.i("Swipeable Cards","I dislike the card");
            }

        });

        card.setOnClickListener(new CardModel.OnClickListener() {
            @Override
            public void OnClickListener() {
                Log.i("Swipeable Cards","I am pressing the card");
            }
        });

        SimpleCardStackAdapter adapter = new SimpleCardStackAdapter(getActivity());
        adapter.add(card);
        mCardContainer.setAdapter(adapter);




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
}

