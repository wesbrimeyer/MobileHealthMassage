package edu.wit.mobilehealth.mobilehealthmassage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class pointListAdapter extends ArrayAdapter<point> {
    private static final String TAG ="pointListAdapter";
    private Context mContext;
    int mResource;

    public pointListAdapter(@NonNull Context context, int resource, ArrayList<point> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String point = getItem(position).getPointName();
        String description = getItem(position).getDescription();

        point p = new point(point,description);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView tPoint = (TextView)convertView.findViewById(R.id.point1);
        TextView tDescription = (TextView)convertView.findViewById(R.id.Description1);

        tPoint.setText(point);
        tDescription.setText(description);

        return convertView;

    }
}
