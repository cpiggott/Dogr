package com.pathogenstudios.dogr.dogr;

/**
 * Created by HaydenKinney on 2/22/15.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseFile;

/**
 * Created by H on 2/15/2015.
 */
public class MatchesAdapter extends ArrayAdapter<Match> {
    Context context;
    int layoutResourceId;
    Match[] data;
    Bitmap bmp;

    public MatchesAdapter(Context context, int layoutResourceId, Match[] data) {

        super(context, layoutResourceId, data);
        Log.d("DOGR:MATCHESADAPTER: ", "Entered constructor of MatchesAdapter");

        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;

        Log.d("DOGR:MATCHESADAPTER: ", "Exited constructor of MatchesAdapter");

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MatchHolder holder = null;

        Log.d("DOGR:MATCHESADAPTER: ", "Entered if block of getView of MatchesAdapter");

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new MatchHolder();
            holder.icon = (ImageView) row.findViewById(R.id.imgViewContactIcon);
            holder.txtName = (TextView) row.findViewById(R.id.txtViewContactName);
            holder.txtContactInfo = (TextView) row.findViewById(R.id.txtViewContactMethod);

            row.setTag(holder);
        } else {
            holder = (MatchHolder) row.getTag();
        }

        Log.d("DOGR:MATCHESADAPTER: ", "Exited if block of getView of MatchesAdapter");

        Match match = data[position];
        ParseFile thumbnailFile = match.getThumbnailFile();
        String name = match.getName();
        String number = match.getNumber();
//        holder.icon.setImageResource(contact.getIcon());

        holder.txtName.setText(name);
        try {
            holder.txtContactInfo.setText(number);
        } catch (Exception e) {
            Toast.makeText(this.getContext(), "Unable to retrieve contact info.", Toast.LENGTH_LONG).show();
        }
        holder.icon.setImageResource(R.drawable.ic_action_person);

        if (thumbnailFile != null) {
            try {
                byte[] bytes = thumbnailFile.getData();
                bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.icon.setImageBitmap(bmp);
            } catch (Exception ex) {
                // do nothing with our exception
            }
        }
        return row;
    }

    static class MatchHolder {
        ImageView icon;
        TextView txtName;
        TextView txtContactInfo;
    }

}
