package edu.wit.mobilehealth.mobilehealthmassage;

import android.graphics.drawable.Drawable;

public class MassagePoint {
    private String pointName;
    private String description;
    private Drawable pointImage;
    private int vibratorPin;
    boolean isOn;
    final boolean isFlipped;

    public MassagePoint(String pointName, String description, Drawable pointImage, int vibratorPin, boolean isFlipped) {
        this.description = description;
        this.pointName = pointName;
        this.pointImage = pointImage;
        this.vibratorPin = vibratorPin;
        this.isOn = false;
        this.isFlipped = isFlipped;
    }

    public String getPointName() {
        return pointName;
    }

    public String getDescription() {
        return description;
    }

    public Drawable getPointImage() {
        return pointImage;
    }

    public int getVibratorPin() {
        return vibratorPin;
    }
}
