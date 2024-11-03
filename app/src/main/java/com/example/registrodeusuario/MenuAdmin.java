// MenuAdmin.java
package com.example.registrodeusuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_admin);

        // Obtener los datos del usuario del Intent
        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        final String firstName = intent.getStringExtra("firstName");
        final String lastName = intent.getStringExtra("lastName");
        final String role = intent.getStringExtra("role");

        // Botón para la lista de usuarios
        Button userListButton = findViewById(R.id.buttonAdministrate);
        userListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userListIntent = new Intent(MenuAdmin.this, UserList.class);
                userListIntent.putExtra("username", username);
                userListIntent.putExtra("firstName", firstName);
                userListIntent.putExtra("lastName", lastName);
                userListIntent.putExtra("role", role);
                startActivity(userListIntent);
            }
        });

        // Botón para la creación de propuestas
        Button proposalCreationButton = findViewById(R.id.buttonCreacionPropuesta);
        proposalCreationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent proposalCreationIntent = new Intent(MenuAdmin.this, CreationProposal.class);
                proposalCreationIntent.putExtra("username", username);
                proposalCreationIntent.putExtra("firstName", firstName);
                proposalCreationIntent.putExtra("lastName", lastName);
                proposalCreationIntent.putExtra("role", role);
                startActivity(proposalCreationIntent);
            }
        });

        // Botón para la lista de propuestas
        Button proposalListButton = findViewById(R.id.buttonListaPropuestas);
        proposalListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent proposalListIntent = new Intent(MenuAdmin.this, ProposalList.class);
                proposalListIntent.putExtra("username", username);
                proposalListIntent.putExtra("firstName", firstName);
                proposalListIntent.putExtra("lastName", lastName);
                proposalListIntent.putExtra("role", role);
                startActivity(proposalListIntent);
            }
        });

        // Botón para los resultados
        Button resultsButton = findViewById(R.id.buttonResults);
        resultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultsIntent = new Intent(MenuAdmin.this, Results.class);
                resultsIntent.putExtra("username", username);
                resultsIntent.putExtra("firstName", firstName);
                resultsIntent.putExtra("lastName", lastName);
                resultsIntent.putExtra("role", role);
                startActivity(resultsIntent);
            }
        });

        // Botón para administrar
        Button administrateButton = findViewById(R.id.buttonAdministrate);
        administrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent administrateIntent = new Intent(MenuAdmin.this, Administrator.class);
                administrateIntent.putExtra("username", username);
                administrateIntent.putExtra("firstName", firstName);
                administrateIntent.putExtra("lastName", lastName);
                administrateIntent.putExtra("role", role);
                startActivity(administrateIntent);
            }
        });

        // Botón para el mapa
        Button mapsButton = findViewById(R.id.buttonMaps);
        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapsIntent = new Intent(MenuAdmin.this, Maps.class);
                startActivity(mapsIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Crear un intent para ir a MainActivity
        super.onBackPressed();
        Intent intent = new Intent(MenuAdmin.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        // Llamar a finish para cerrar la actividad actual
        finish();
    }
}