package com.example.student_v1;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ViewEvents extends BaseActivity implements AdapterView.OnItemSelectedListener {

    DatabaseReference myRef;
    FirebaseDatabase database;
    List<Event> events = new ArrayList<Event>();
    List<String> attended=new ArrayList<String>();

    RecyclerView recyclerView;
    MyAdapter adapterForUpdate;
    String uid;

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat todayFormatter = new SimpleDateFormat("yyyy-MM-dd");
    Date todayDate;
    int dateComparisonFlag=0;

    {
        try {
            Log.d("datew", "instance initializer: "+LocalDate.now());
            todayDate = todayFormatter.parse(String.valueOf(LocalDate.now()));
            Log.d("datew", "instance initializer2: "+todayDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    int spinnerValue=101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        recyclerView = findViewById(R.id.recyclerview2);

        Spinner dropdown = findViewById(R.id.spinner2);
        //String[] ViewString = new String[]{"Archived Events", "Today's Events", "Future Events"};
        String[] events2 = new String[]{"Events attended","Today's events","Future Events"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, events2);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            uid = (String) bundle.get("uid");
        }

        database = FirebaseDatabase.getInstance("https://eccloginmoduletest-default-rtdb.asia-southeast1.firebasedatabase.app/");
        myRef = database.getReference("events");
        Log.d("abc", "onComplete: " + uid);

        // getting attended events start
        database.getReference("users").child("" + uid).child("eventsAttended").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Log.d("uid", "onComplete: " + uid);
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    attended= (List<String>) task.getResult().getValue();
                    if(attended != null) {
                        while (attended.remove(null)) {
                        }
                    }

                    Log.d("lol", "array data "+attended);
                }
            }
        });
        Log.d("lol", "array data after that ends"+attended);

//getting attended events end

    }

    public void updateRecycler2() {

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                HashMap value = (HashMap) dataSnapshot.getValue();
                if (value==null){
                    Toast.makeText(ViewEvents.this, "There are no events!", Toast.LENGTH_SHORT).show();
                }else{

                    events.clear();  //clearing the array, basically the screen first using this
                    for (Object i : value.keySet()) {
//                            events.add(new Event(((HashMap) value.get(i)).get("name") + "",
//                                    ((HashMap) value.get(i)).get("department") + "",
//                                    ((HashMap) value.get(i)).get("faculty") + "",
//                                    ((HashMap) value.get(i)).get("points") + "",
//                                    ((HashMap) value.get(i)).get("date")+"",
//                                    ((HashMap) value.get(i)).get("time") + ""));
                        Log.d("indexx", "onComplete: "+i);
                        Date date = null;
                        try {
                            date = formatter.parse(((HashMap) value.get(i)).get("date")+"");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
//                        Log.d("datee", "onComplete: "+LocalDate.now());


                        try {
                            dateComparisonFlag=date.compareTo(todayDate);

                            if(dateComparisonFlag > 0) //future date
                            {
                                if (spinnerValue==102){ //future event
//                                    Log.d("dbdb", "dbdb "+((HashMap) value.get(i)).get("venue"));
                                    events.add(new Event(i + "",
                                            ((HashMap) value.get(i)).get("name") + "",
                                            ((HashMap) value.get(i)).get("department") + "",
                                            ((HashMap) value.get(i)).get("venue") + "",
                                            ((HashMap) value.get(i)).get("faculty") + "",
                                            ((HashMap) value.get(i)).get("points") + "",
                                            ((HashMap) value.get(i)).get("date")+"",
                                            ((HashMap) value.get(i)).get("time") + ""));
                                }
                                Log.d("cccomp", date+" is future");
                            }
                            else if(dateComparisonFlag == 0) //todays
                            {
                                if (spinnerValue==100){ //todaysevents
                                    events.add(new Event(i + "",
                                            ((HashMap) value.get(i)).get("name") + "",
                                            ((HashMap) value.get(i)).get("department") + "",
                                            ((HashMap) value.get(i)).get("venue") + "",
                                            ((HashMap) value.get(i)).get("faculty") + "",
                                            ((HashMap) value.get(i)).get("points") + "",
                                            ((HashMap) value.get(i)).get("date")+"",
                                            ((HashMap) value.get(i)).get("time") + ""));
                                }
                                Log.d("cccomp", date+" is todayy");
                            }
                            else if(dateComparisonFlag == -1) //past event
                            {
                                if (spinnerValue==103){ //attended tab
                                    if (attended.contains(i+"")){
                                        events.add(new Event(i + "",
                                                ((HashMap) value.get(i)).get("name") + "",
                                                ((HashMap) value.get(i)).get("department") + "",
                                                ((HashMap) value.get(i)).get("venue") + "",
                                                ((HashMap) value.get(i)).get("faculty") + "",
                                                ((HashMap) value.get(i)).get("points") + "",
                                                ((HashMap) value.get(i)).get("date")+"",
                                                ((HashMap) value.get(i)).get("time") + ""));
                                    }else{
                                        Log.d("lol", "doesnt exist: "+i);
                                        Log.d("lol", "here: "+attended);
                                    }
                                    Log.d("dbab", "id "+i);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

//                            System.out.println("Date object value: "+date);

                    }

                    sort(events);

//                        adapterForUpdate=new MyAdapter2(getApplicationContext(),events);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerView.setAdapter(new MyAdapter(getApplicationContext(), events));
//                        recyclerView.setAdapter(adapterForUpdate); //testing for update

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

    }



    public void sort(List<Event> events) {

        events.sort(Comparator.comparing(o -> {
            try {
                return formatter.parse(o.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }));
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String choice=adapterView.getItemAtPosition(i).toString();
        if (choice=="Today's events"){
            spinnerValue=100;
        }else if(choice=="Future Events"){
            spinnerValue=102;
        }else if(choice=="Events attended"){
            spinnerValue=103;
        }
        updateRecycler2();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}