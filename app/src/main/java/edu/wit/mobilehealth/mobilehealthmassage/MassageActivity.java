package edu.wit.mobilehealth.mobilehealthmassage;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class MassageActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityMassage);

        listView =(ListView)findViewById(R.id.ListView01);

        point p1 = new point("p1","This point helps ...");
        point p2 = new point("p2","This point helps ...");
        point p3 = new point("p3","This point helps ...");
        point p4 = new point("p4","This point helps ...");
        ArrayList<point> arrayList= new ArrayList<>();

        arrayList.add(p1);
        arrayList.add(p2);
        arrayList.add(p3);
        arrayList.add(p4);

        pointListAdapter adapter = new pointListAdapter(this,R.layout.adapter_view,arrayList);
        listView.setAdapter(adapter);



    }

}
