package com.example.chattest;

import android.R.layout;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllUsers extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private ListView listView;
    private ArrayList<UserInformation> arrayList = new ArrayList<>();
    private ArrayList<String> display = new ArrayList<>();
    private ArrayAdapter<UserInformation> adapter;
    private ArrayAdapter<String> displayAdapter;
    private Button search;
    private EditText field;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_users);
        search = (Button) findViewById(R.id.button4);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                field = (EditText) findViewById(R.id.editText2);
                mDatabase.orderByChild("1").equalTo(field.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            Log.d("Testing output2", "PARENT: " + childDataSnapshot.getKey());
                            Log.d("Testing output2", (String) childDataSnapshot.child("name").getValue());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                }
                );
                mDatabase.orderByChild("2").equalTo(field.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            Log.d("Testing output2", "PARENT: " + childDataSnapshot.getKey());
                            Log.d("Testing output2", (String) childDataSnapshot.child("name").getValue());
                            Log.d("Testing output2", (String) childDataSnapshot.child("email").getValue());

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                }
                );
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        adapter = new ArrayAdapter<UserInformation>(this, android.R.layout.simple_list_item_1, arrayList);
        displayAdapter = new ArrayAdapter<String>(this, layout.simple_list_item_1, display);
        listView = (ListView) findViewById(R.id.list_of_users);
        listView.setAdapter(displayAdapter);
        ValueEventListener eventListener = new ValueEventListener() {
            //once the button is clicked, it access firebase & retrieve all info about all users
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
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
            public void onCancelled(DatabaseError databaseError) {}
        };
        mDatabase.addListenerForSingleValueEvent(eventListener);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserInformation usertest = adapter.getItem(position);
                AlertDialog alertDialog = new AlertDialog.Builder(AllUsers.this).create();
                alertDialog.setTitle("iNFO");
                alertDialog.setMessage("Name:" + usertest.getName() + usertest.getEmail() + usertest.getInterest());
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                usertest.setName("");

            }
        });

    }
}
