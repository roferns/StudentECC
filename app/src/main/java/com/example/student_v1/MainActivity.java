package com.example.student_v1;

//v1: added qr generation
//v1.2: added login,
//      sxc acc verification
//      app now directly takes student data from db

/////////////////////////////////////////////////////////////////
//
// v2: includes all UI changes by rochele   date: 6/4/23
//      fixed classTv issues
//      added viewEvents
//      added attended events


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends BaseActivity {

    TextView nameTV,scoreTV, welcomeTV, uidTV, emailTV, className;
    Button viewEventsBtn;

    String name, uid,classTitle;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button submit;
//        EditText name,uid,className;
//        name=findViewById(R.id.editTextForName);
//        uid=findViewById(R.id.editTextForUID);
        className=findViewById(R.id.class_tv2);
        submit=findViewById(R.id.qr_btn);
        emailTV = findViewById(R.id.email_tv2);
        viewEventsBtn=findViewById(R.id.events_btn);

//        verifying valid sxc acc starts here
        //nameTV=findViewById(R.id.textView);
        welcomeTV = findViewById(R.id.welcome_tv);
        //logoutBtn=findViewById(R.id.button);
        scoreTV=findViewById(R.id.score_tv);

        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc= GoogleSignIn.getClient(this,gso);

        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(this);

//        firebase assistant code for reading starts here
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://eccloginmoduletest-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("users");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                HashMap value = (HashMap) snapshot.getValue();


//                inefficient method, change this later
                int flag=0;
                String scoreMessage="";
                for(Object s: value.keySet()){
                    HashMap switchMap = (HashMap) value.get(s);
                    if(switchMap.containsValue(account.getEmail())){
                        welcomeTV.setText("Welcome "+switchMap.get("name"));
                        name= (String) switchMap.get("name");
                        uid= (String) s;
                        emailTV.setText((String) switchMap.get("email"));
//                        className.setText(""+switchMap.get("class"));
//                        className.setText(""+
//                        database.getReference("class").child(""+switchMap.get("class")).get()
//                        );
                        database.getReference("class").child(""+switchMap.get("class")).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
//                                    Log.e("firebase", "Error getting data", task.getException());
                                }
                                else {
                                    className.setText(""+task.getResult().getValue());
                                    classTitle =""+task.getResult().getValue();

//                                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                }
                            }
                        });
//                        scoreTV.setText( "Ecc score: "+(Long)switchMap.get("score"));
//                        scoreMessage="Ecc score: "+switchMap.get("score");
                        scoreTV.setText(""+switchMap.get("score"));
                        if (Integer.parseInt(""+switchMap.get("score"))>=10){
                            scoreTV.setTextColor(Color.parseColor("#118042"));
                        }else {
                            scoreTV.setTextColor(Color.parseColor("#CF3737"));

                        }
                        Log.d("database", "onDataChange: "+switchMap.get("score"));
                        flag=1;
                    }
                }
//                scoreTV.setText(scoreMessage);
                if (flag==0){
                    Toast.makeText(getApplicationContext(), "not a valid sxc acc", Toast.LENGTH_SHORT).show();
                    logOut();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("database error", "Failed to read value.", error.toException());
            }
        });
//        firebase assistant code for reading ends here
//        verifying valid sxc acc ends here

//        logoutBtn.setOnClickListener(view -> {
//            logOut();
//
//        });


        viewEventsBtn.setOnClickListener(view -> {
            Intent i = new Intent(this,ViewEvents.class);
            i.putExtra("uid", uid);
            startActivity(i);
        });
        submit.setOnClickListener(view -> {
            Intent i = new Intent(this,MainActivity2.class);
//            i.putExtra("message_key", name.getText()+"#"+uid.getText()+"#"+className.getText());
            i.putExtra("message_key", name+"#"+uid+"#"+classTitle);
            Log.d("dbab", "onCreate: "+name+" "+uid+" "+classTitle);
            startActivity(i);
        });

    }

    //menu start
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //menu end

    public void logOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });
    }
}