package com.example.chattest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.text.format.DateFormat;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {

    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<Message> adapter;
    RelativeLayout activity_main;
    FloatingActionButton sendMessage;
    //Button button2;
    Button button3;
    public FirebaseDatabase mFirebaseDatabase;
    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;
    public DatabaseReference myRef;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                Snackbar.make(activity_main, "Success", Snackbar.LENGTH_SHORT).show();
                displayChatMessage();
            }
            else
            {
                Snackbar.make(activity_main, "Sorry, please try again", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.sign_out)
        {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(MainActivity.this, MainActivity.class));
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
        setContentView(R.layout.activity_main);
        activity_main = (RelativeLayout) findViewById(R.id.activity_main);
        sendMessage = (FloatingActionButton) findViewById(R.id.sendButton);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText) findViewById(R.id.input);
                FirebaseDatabase.getInstance().getReference().push().setValue(new Message(input.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
                input.setText("");
            }
        });
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);

        }
        else
        {
            Snackbar.make(activity_main, "Welcome! " + FirebaseAuth.getInstance().getCurrentUser().getEmail(),Snackbar.LENGTH_SHORT).show();
            displayChatMessage();
            mAuth = FirebaseAuth.getInstance();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            myRef = mFirebaseDatabase.getReference();
            //go to edit information page
        /*    button2 = findViewById(R.id.button2);
            button2.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    System.out.println("Clicked!");
                    Intent intent = new Intent(MainActivity.this, editInformation.class);
                    startActivity(intent);
                }
            });*/
            //go to all users page
            button3 = findViewById(R.id.button3);
            button3.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    System.out.println("Clicked!");
                    Intent intent = new Intent(MainActivity.this, AllUsers.class);
                    startActivity(intent);
                }
            });

        }
    }

    private void displayChatMessage() {
        ListView listOfMessage = (ListView) findViewById(R.id.list_of_messages);
        adapter = new FirebaseListAdapter<Message>(this,Message.class,R.layout.list_item,FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, Message model, int position) {
                TextView messageText,messageUser,messageTime;
                messageText = (TextView) v.findViewById(R.id.msg_text);
                messageUser = (TextView) v.findViewById(R.id.msg_sender);
                messageTime = (TextView) v.findViewById(R.id.msg_time);
                messageText.setText(model.getMessage());
                messageUser.setText(model.getSender());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getTime()));
            }
        };
        listOfMessage.setAdapter(adapter);
    }
}
