package com.wojtekadam.mirae;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;


public class MainScreenActivity extends Activity {

    Button btnViewUsers;
    Button btnAddUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        // Buttons
        btnViewUsers =  (Button) findViewById(R.id.btnViewUsers);
        btnAddUser = (Button) findViewById(R.id.btnAddUser);

        // view users click event
        btnViewUsers.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Launching All users Activity
                Intent i = new Intent(getApplicationContext(), AllUsersActivity.class);
                startActivity(i);
            }
        });

        //view users click event
        btnAddUser.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                // Launching add new user activity
                Intent i = new Intent(getApplicationContext(), NewUserActivity.class);
                startActivity(i);
            }
        });
    }
}
