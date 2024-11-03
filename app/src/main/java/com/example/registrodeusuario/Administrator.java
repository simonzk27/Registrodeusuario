// Administrator.java
package com.example.registrodeusuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Administrator extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.administrator_activity);

        // Obtener los datos del usuario del Intent
        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        final String firstName = intent.getStringExtra("firstName");
        final String lastName = intent.getStringExtra("lastName");
        final String role = intent.getStringExtra("role");

        Button restartVotesButton = findViewById(R.id.buttonRestartVotes);
        Button userListButton = findViewById(R.id.buttonListaUsuarios);
        Button oldProposalsButton = findViewById(R.id.buttonOldProposals);

        restartVotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDatabaseHelper dbHelper = new UserDatabaseHelper(Administrator.this);
                boolean proposalsReset = dbHelper.resetAllProposals();
                boolean usersReset = dbHelper.resetAllUsers();

                if (proposalsReset && usersReset) {
                    Toast.makeText(Administrator.this, "Votes and proposals have been restarted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Administrator.this, "Failed to restart votes and proposals", Toast.LENGTH_SHORT).show();
                }
            }
        });

        userListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userListIntent = new Intent(Administrator.this, UserList.class);
                userListIntent.putExtra("username", username);
                userListIntent.putExtra("firstName", firstName);
                userListIntent.putExtra("lastName", lastName);
                userListIntent.putExtra("role", role);
                startActivity(userListIntent);
            }
        });

        oldProposalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent oldProposalsIntent = new Intent(Administrator.this, OldProposals.class);
                oldProposalsIntent.putExtra("username", username);
                oldProposalsIntent.putExtra("role", role);
                startActivity(oldProposalsIntent);
            }
        });
    }
}