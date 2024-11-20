// MenuUsuario.java
package com.example.registrodeusuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuUsuario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_user);

        // Obtener los datos del usuario del Intent
        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        final String firstName = intent.getStringExtra("firstName");
        final String lastName = intent.getStringExtra("lastName");
        final String role = intent.getStringExtra("role");

        // Botón para la lista de propuestas
        Button proposalListButton = findViewById(R.id.buttonPropuestas);
        proposalListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent proposalListIntent = new Intent(MenuUsuario.this, ProposalList.class);
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
                Intent resultsIntent = new Intent(MenuUsuario.this, Results.class);
                resultsIntent.putExtra("username", username);
                resultsIntent.putExtra("firstName", firstName);
                resultsIntent.putExtra("lastName", lastName);
                resultsIntent.putExtra("role", role);
                startActivity(resultsIntent);
            }
        });

        // Botón para el mapa
        Button mapsButton = findViewById(R.id.buttonMaps);
        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapsIntent = new Intent(MenuUsuario.this, Maps.class);
                startActivity(mapsIntent);
            }
        });
    }


    @Override
    public void onBackPressed() {
        // Crear un intent para ir a MainActivity
        super.onBackPressed();
        Intent intent = new Intent(MenuUsuario.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        // Llamar a finish para cerrar la actividad actual
        finish();
    }
}