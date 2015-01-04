package com.wojtekadam.mirae;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainScreenActivity extends Activity {

    Button btnViewUsers;
    Button btnAddUser;
//    Button btnKalendarz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        // Buttons
        btnViewUsers =  (Button) findViewById(R.id.btnViewUsers);
        btnAddUser = (Button) findViewById(R.id.btnAddUser);
//        btnKalendarz = (Button) findViewById(R.id.Kalendarz);

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
//        btnKalendarz.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//        public void onClick(View view){
//                Intent i = new Intent(getApplicationContext(), PickADateActivity.class);
//                startActivity(i);
//            }
//        });


    }
}
