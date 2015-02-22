package com.pathogenstudios.dogr.dogr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/**
 * Created by HaydenKinney on 2/21/15.
 */
/**
 * A placeholder fragment containing a simple view.
 */
public class MatchesFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "5";
    ListView list;
    ArrayList<Match> matchesList;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MatchesFragment newInstance(int sectionNumber) {
        MatchesFragment fragment = new MatchesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MatchesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_matched_users, container, false);
//        userImage = (ImageButton) rootView.findViewById(R.id.imageButtonUpdateUser);

        list = (ListView)rootView.findViewById(R.id.listViewMatches);

        LoadMatchesAsync lma = new LoadMatchesAsync(getActivity(), list, this);
        lma.execute();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<String> builderOptions = new ArrayList<String>();
                builderOptions.add("Revisit their profile!");
                builderOptions.add("Send them an email!");

                CharSequence[] cM = new CharSequence[builderOptions.size()];
                cM = builderOptions.toArray(cM);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Get In Touch!");
                builder.setItems(cM, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0) {
                            // switch fragment to their profile
                        } else if( which == 1 ) {
                            sendAppInviteEmail(matchesList.get(0).getNumber());
                        }
                    }
                });
                builder.show();
            }
        });

        return rootView;

    }

    public void sendAppInviteEmail(String email) {
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "You've been matched up for a Dogr Playdate!");
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("<h1><small>Hello!</small></h1>\n" +
                "\n" +
                "<p>Looks like you and me matched up on Dogr - we should schedule a playdate! We can exchange phone numbers over email or just determine a place to meet up! Hope to see you and your pets soon!</p>"));

        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    public void asyncResult(ArrayList<Match> mL) {
        matchesList = mL;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}


class LoadMatchesAsync extends AsyncTask<Void, Void, ArrayList<Match>> {
    ProgressDialog progressDialog;
    Activity mActivity;
    ListView matchesList;
    MatchesFragment mf;
    ArrayList<Match> matches;

    public LoadMatchesAsync(Activity activity, ListView matchesList, MatchesFragment mf ) {
        mActivity = activity;
        this.matchesList = matchesList;
        progressDialog = new ProgressDialog( mActivity );
        this.mf = mf;
        matches  = new ArrayList<Match>();
    }

    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Loading Matches");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    protected ArrayList<Match> doInBackground(Void... params) {

        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Swipe");
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Swipe");

        query1.whereEqualTo("userOne", currentUser);
        query1.whereEqualTo("userOneSwipe", "y");
        query1.whereEqualTo("userTwoSwipe", "y");

        query2.whereEqualTo("userTwo", currentUser);
        query2.whereEqualTo("userOneSwipe", "y");
        query2.whereEqualTo("userTwoSwipe", "y");
        try{
            List<ParseObject> matchesList = query1.find();
            for(int i = 0; i < matchesList.size(); i++) {
                ParseUser p = (ParseUser)matchesList.get(i).get("userTwo");
                p = p.fetchIfNeeded();
                Match m = new Match((p.getString("firstName") + " " + p.getString("lastName")), p.getEmail(), p.getParseFile("userProfileImage"));
                matches.add(m);
            }
        } catch(Exception ex) {
            Log.d("MATCHESFRAG:", "There was an error! : " + ex.toString());
        }
        try{
            List<ParseObject> matchesList = query2.find();
            for(int i = 0; i < matchesList.size(); i++) {
                ParseUser p = (ParseUser)matchesList.get(i).get("userOne");
                p = p.fetchIfNeeded();
                Match m = new Match((p.getString("firstName") + " " + p.getString("lastName")), p.getEmail(), p.getParseFile("userProfileImage"));
                matches.add(m);
            }
        } catch(Exception ex) {
            Log.d("MATCHESFRAG:", "There was an error! : " + ex.toString());
        }


//        query1.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> matchesList, ParseException e) {
//                if( e == null ) {
//                    for(int i = 0; i < matchesList.size(); i++) {
//                        ParseUser p = (ParseUser)matchesList.get(0).get("UserTwo");
//                        Match m = new Match((p.getString("firstName") + " " + p.getString("lastName")), p.getEmail(), p.getParseFile("userProfileImage"));
//                        matches.add(m);
//                    }
//                }
//            }
//        });
//
//        query2.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> matchesList, ParseException e) {
//                if( e == null ) {
//                    for(int i = 0; i < matchesList.size(); i++) {
//                        ParseUser p = (ParseUser)matchesList.get(0).get("UserOne");
//                        Match m = new Match((p.getString("firstName") + " " + p.getString("lastName")), p.getEmail(), p.getParseFile("userProfileImage"));
//                        matches.add(m);
//                    }
//                }
//            }
//        });
        Collections.sort(matches, new Comparator<Match>() {
            public int compare(Match m1, Match m2) {
                return m1.getName().compareTo(m2.getName());
            }
        });

        return matches;
    }

    protected void onPostExecute(ArrayList<Match> matches) {
        super.onPostExecute(matches);
        mf.asyncResult(matches);
        Match[] matchesArray = new Match[matches.size()];
        matchesArray = matches.toArray(matchesArray);
        MatchesAdapter customAdapter = new MatchesAdapter( mActivity, R.layout.item_matched_user, matchesArray);
        matchesList.setAdapter(customAdapter);
        progressDialog.cancel();
    }
}
