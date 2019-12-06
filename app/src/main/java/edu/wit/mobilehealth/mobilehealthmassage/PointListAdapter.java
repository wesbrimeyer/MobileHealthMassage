package edu.wit.mobilehealth.mobilehealthmassage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class PointListAdapter extends ArrayAdapter<MassagePoint> {
    private static final String TAG ="PointListAdapter";
    private Context mContext;
    private int mResource;
    private ArduinoController arduino;

    public PointListAdapter(@NonNull Context context, int resource, ArrayList<MassagePoint> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final MassagePoint p = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView tPoint = convertView.findViewById(R.id.point_title);
        TextView tDescription = convertView.findViewById(R.id.Description1);

        assert p != null;
        imageView.setImageDrawable(p.getPointImage());
        if (p.isFlipped) { imageView.setRotationY(180); }
        tPoint.setText(p.getPointName());
        tDescription.setText(p.getDescription());

        //TODO set on click listener to turn on/off vibrator
        final Button triggerButton = convertView.findViewById(R.id.triggerButton);
        triggerButton.setEnabled(arduino != null);
        triggerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.isOn = !p.isOn;
                if (p.isOn) {
                    arduino.turnOnPin(p.getVibratorPin());
                    triggerButton.setText(mContext.getString(R.string.turn_off));

                } else {
                    arduino.turnOffPin(p.getVibratorPin());
                    triggerButton.setText(mContext.getString(R.string.turn_on));
                }
            }
        });

        return convertView;
    }

    void connectArduino(ArduinoController arduinoController) {
        arduino = arduinoController;
        notifyDataSetChanged();
    }
}
