package com.example.chattest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query;
public class editInformation extends AppCompatActivity {
    private static final String TAG = "AddToDatabase";

    private Button btnSubmit;
    private EditText mName,mEmail,mInterest;
    public String userID;
    private int count = 0;

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private CheckBox[] cb = new CheckBox[4];
    private int i;
    String interest1;
    String interest2;
    String interest3;
    String interest4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = rootRef.child("users");
        ValueEventListener eventListener = new ValueEventListener() {
            //once the button is clicked, it access firebase & retrieve all info about all users
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String getEmail = ds.child("email").getValue(String.class);
                    String getName = ds.child("name").getValue(String.class);
                    String getInterest1 = ds.child("1").getValue(String.class);
                    String getInterest2 = ds.child("2").getValue(String.class);
                    Log.d("Testing output", getEmail + " / " + getName + getInterest1 + getInterest2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        usersRef.addListenerForSingleValueEvent(eventListener);
        setContentView(R.layout.edit_info);
        //retrieve button from xml
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        mName = (EditText) findViewById(R.id.etName);
        mEmail = (EditText) findViewById(R.id.etEmail);
        cb[0] = (CheckBox) findViewById(R.id.cnBinterest1);
        cb[1] = (CheckBox) findViewById(R.id.cnBinterest2);
        cb[2] = (CheckBox) findViewById(R.id.cnBinterest3);
        cb[3] = (CheckBox) findViewById(R.id.cnBinterest4);

        //firebase auth
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        //get user id from firebase authentication, see who is logged in for now
        userID = user.getUid();
        mEmail.setText(user.getEmail());
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    toastMessage("You are now modifying the user information with email:" + user.getEmail());
                } else {
                    toastMessage("Successfully signed out.");
                }
            }
        };
        interest1 = cb[0].getText().toString();
        interest2 = cb[1].getText().toString();
        interest3 = cb[2].getText().toString();
        interest4 = cb[3].getText().toString();
        for (i = 0; i < 4; i++) {
            cb[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (count == 2 && isChecked) {
                        toastMessage("Please check only two boxes");
                        cb[0].setChecked(false);
                        cb[1].setChecked(false);
                        cb[2].setChecked(false);
                        cb[3].setChecked(false);
                    } else if (isChecked) {
                        count++;
                    } else if (!isChecked) {
                        count--;
                    }
                }
            });
        }

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Entry adding to database: \n" +
                        dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Error:", error.toException());
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mName.getText().toString();
                String email = mEmail.getText().toString();
                    //all fields are required, checking if any of them are empty
                DatabaseReference current_user_db = myRef.child("users").child(userID);
                UserInformation userInformation = new UserInformation(name,email);
                //grab database object users
                current_user_db.setValue(userInformation);
                toastMessage("Infomation saved!");
                int id = 0;

                if(!name.equals("") && !email.equals("")){
                    if(cb[0].isChecked()) {
                        id++;
                        current_user_db.child(String.valueOf(id)).setValue(interest1);
                    }
                    if(cb[1].isChecked()) {
                        id++;
                        current_user_db.child(String.valueOf(id)).setValue(interest2);
                    }
                    if(cb[2].isChecked()) {
                        id++;
                        current_user_db.child(String.valueOf(id)).setValue(interest3);

                    }
                    if(cb[3].isChecked()) {
                        id++;
                        current_user_db.child(String.valueOf(id)).setValue(interest4);
                    }
                    id=0;
                    mName.setText("");
                    mEmail.setText("");
                }else{
                    toastMessage("All fields required");
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
