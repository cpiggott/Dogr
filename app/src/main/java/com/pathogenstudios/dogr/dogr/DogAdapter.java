package com.pathogenstudios.dogr.dogr;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DogAdapter extends ArrayAdapter<Dog> {
    Context context;
    int layoutResourceId;
    Dog[] data;
//    Bitmap bmp;

    public DogAdapter(Context context, int layoutResourceId, Dog[] data) {

        super(context, layoutResourceId, data);
        Log.d("DOGR:MATCHESADAPTER: ", "Entered constructor of MatchesAdapter");

        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;

        Log.d("DOGR:MATCHESADAPTER: ", "Exited constructor of MatchesAdapter");

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        DogHolder holder = null;

        Log.d("DOGR:MATCHESADAPTER: ", "Entered if block of getView of MatchesAdapter");

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new DogHolder();
            holder.txtName = (TextView) row.findViewById(R.id.txtViewDogName);
            holder.txtDailyGoal = (TextView) row.findViewById(R.id.txtViewDailyGoal);
            holder.txtGender = (TextView) row.findViewById(R.id.txtViewGender);
            holder.txtBreed = (TextView) row.findViewById(R.id.txtViewBreed);
            holder.txtWeight = (TextView) row.findViewById(R.id.txtViewWeight);
//            holder.imgView = (ImageView)row.findViewById(R.id.profilePic);

            row.setTag(holder);
        } else {
            holder = (DogHolder) row.getTag();
        }

        Log.d("DOGR:MATCHESADAPTER: ", "Exited if block of getView of MatchesAdapter");

        Dog match = data[position];
        String name = match.getName();
        String gender = match.getGender();
        String breed = match.getBreed();
        String dailyGoal = match.getDailyGoal();
        String weight = match.getWeight();


//        holder.imgView.setImageResource(R.drawable.ic_action_person);
        holder.txtName.setText(name);
        holder.txtGender.setText(gender);
        holder.txtBreed.setText(breed);
        holder.txtDailyGoal.setText("Daily Activity: " + dailyGoal);
        holder.txtWeight.setText("Weight: " + weight);

        return row;
    }

    static class DogHolder {
//        ImageView imgView;
        TextView txtName;
        TextView txtBreed;
        TextView txtGender;
        TextView txtDailyGoal;
        TextView txtWeight;
    }

}
