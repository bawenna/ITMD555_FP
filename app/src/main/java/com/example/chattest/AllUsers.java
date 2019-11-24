package com.example.chattest;

import android.R.layout;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllUsers extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    private static int SIGN_IN_REQUEST_CODE = 1;
    private DatabaseReference mDatabase;
    private ListView listView;
    private RelativeLayout all_users;
    private ArrayList<UserInformation> arrayList = new ArrayList<>();
    private ArrayList<String> display = new ArrayList<>();
    private ArrayAdapter<UserInformation> adapter;
    private ArrayAdapter<String> displayAdapter;
    private Button search;
    private Button toChatroom;
    private EditText field;
    private StringBuilder s = new StringBuilder();
    public String[] skillNames={"Data Entry","Graphic Design","Programming(Node.js)","Programming(java)"};
    private String retrieval;
    Button button2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                display();
            } else {
                finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sign_out) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(AllUsers.this, AllUsers.class));
                            finish();
                        }
                    });
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        all_users = (RelativeLayout) findViewById(R.id.all_users);
        setContentView(R.layout.all_users);
        Spinner spin = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,skillNames);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);
        spin.setOnItemSelectedListener(this);
        search = (Button) findViewById(R.id.button4);
        toChatroom = (Button) findViewById(R.id.button5);
        toChatroom.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllUsers.this, MainActivity.class);
                startActivity(intent);
            }
        });
         //go to edit information page
        button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Clicked!");
                Intent intent = new Intent(AllUsers.this, editInformation.class);
                startActivity(intent);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.orderByChild("1").equalTo(retrieval).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            s.append("\n");
                            s.append((String) childDataSnapshot.child("name").getValue());
                            s.append("\n");
                            s.append((String) childDataSnapshot.child("email").getValue());
                            Log.d("Testing output2", "PARENT: " + childDataSnapshot.getKey());
                            Log.d("Testing output2", (String) childDataSnapshot.child("name").getValue());
                            Log.d("Testing stringbuilder", s.toString());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                mDatabase.orderByChild("2").equalTo(retrieval).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            Log.d("Testing output2", "PARENT: " + childDataSnapshot.getKey());
                            Log.d("Testing output2", (String) childDataSnapshot.child("name").getValue());
                            Log.d("Testing output2", (String) childDataSnapshot.child("email").getValue());
                            s.append("\n");
                            s.append((String) childDataSnapshot.child("name").getValue());
                            s.append("\n");
                            s.append((String) childDataSnapshot.child("email").getValue());
                        }
                        AlertDialog alertDialog = new AlertDialog.Builder(AllUsers.this).create();
                        alertDialog.setTitle("Result");
                        alertDialog.setMessage(s);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                s = new StringBuilder();
            }
        });
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        } else {
            display();
        }
    }

    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        retrieval = skillNames[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void display()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        adapter = new ArrayAdapter<UserInformation>(this, android.R.layout.simple_list_item_1, arrayList);
        displayAdapter = new ArrayAdapter<String>(this, layout.simple_list_item_1, display);
        listView = (ListView) findViewById(R.id.list_of_users);
        listView.setAdapter(displayAdapter);
        ValueEventListener eventListener = new ValueEventListener() {
            //once the button is clicked, it access firebase & retrieve all info about all users
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    UserInformation user = (UserInformation) dataSnapshot.getValue(UserInformation.class);
                    String getEmail = ds.child("email").getValue(String.class);
                    user.setEmail(getEmail);
                    String getName = ds.child("name").getValue(String.class);
                    user.setName(getName);
                    String getInterest1 = ds.child("1").getValue(String.class);
                    user.setInterest(getInterest1);
                    String getInterest2 = ds.child("2").getValue(String.class);
                    user.setInterest2(getInterest2);
                    Log.d("Testing output", getEmail + " / " + getName + getInterest1 + getInterest2);
                    display.add(user.getName());
                    arrayList.add(user);
                    displayAdapter.notifyDataSetChanged();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mDatabase.addListenerForSingleValueEvent(eventListener);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserInformation usertest = adapter.getItem(position);
                AlertDialog alertDialog = new AlertDialog.Builder(AllUsers.this).create();
                alertDialog.setTitle(usertest.getName());
                String alert2 = "Email: " + usertest.getEmail();
                String alert3 = "Skill(s): N/A";
                if (usertest.getInterest() != null)
                {
                    alert3 = "Skill(s): " + usertest.getInterest();
                }
                if (usertest.getInterest2() != null)
                {
                    alert3 = "Skill(s): " + usertest.getInterest() + ", " + usertest.getInterest2();
                }
                alertDialog.setMessage(alert2+"\n"+alert3);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                //usertest.setName("");

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
