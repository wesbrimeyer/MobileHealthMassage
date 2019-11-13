package edu.wit.mobilehealth.mobilehealthmassage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView card_view_massage = findViewById(R.id.card_view_massage).findViewById(R.id.textView);
        TextView card_view_EMG = findViewById(R.id.card_view_EMG).findViewById(R.id.textView);
        TextView card_view_history = findViewById(R.id.card_view_history).findViewById(R.id.textView);
        card_view_massage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Card View Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        card_view_massage.setText("Acupuncture Massage");
        card_view_EMG.setText("Measure Muscle Tension");
        card_view_history.setText("History");

    }
}