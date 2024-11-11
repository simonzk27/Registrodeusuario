package com.example.registrodeusuario;

import android.content.Intent;
import android.os.Bundle;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class CreationProposal extends AppCompatActivity {

    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creation_proposal);

        // Obtener los datos del usuario del Intent
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String firstName = intent.getStringExtra("firstName");
        String lastName = intent.getStringExtra("lastName");

        dbHelper = new UserDatabaseHelper(this); // Inicializar dbHelper

        Button createProposalButton = findViewById(R.id.button);
        createProposalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProposal(username, firstName, lastName);
            }
        });
    }

    private void createProposal(String username, String firstName, String lastName) {
        EditText titleEditText = findViewById(R.id.proposal_name);
        EditText descriptionEditText = findViewById(R.id.proposal_description_id);
        Spinner localitySpinner = findViewById(R.id.locality_spinner);
        Spinner durationSpinner = findViewById(R.id.proposal_duration_spinner);

        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String locality = localitySpinner.getSelectedItem().toString();
        String duration = durationSpinner.getSelectedItem().toString();

        if (title.isEmpty() || description.isEmpty() || locality.isEmpty() || duration.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(UserDatabaseHelper.COLUMN_PROPOSAL_TITLE, title);
            values.put(UserDatabaseHelper.COLUMN_PROPOSAL_DESCRIPTION, description);
            values.put(UserDatabaseHelper.COLUMN_PROPOSAL_LOCALITY, locality);
            values.put(UserDatabaseHelper.COLUMN_PROPOSAL_DURATION, duration);
            values.put(UserDatabaseHelper.COLUMN_PROPOSAL_USERNAME, username);
            values.put(UserDatabaseHelper.COLUMN_PROPOSAL_FIRST_NAME, firstName);
            values.put(UserDatabaseHelper.COLUMN_PROPOSAL_LAST_NAME, lastName);
            values.put(UserDatabaseHelper.COLUMN_PROPOSAL_STATUS, 1);

            long newRowId = db.insert(UserDatabaseHelper.TABLE_PROPOSALS, null, values);

            if (newRowId == -1) {
                Toast.makeText(this, "Error al crear la propuesta", Toast.LENGTH_SHORT).show();
                System.out.println("CreationProposal " + "Error al insertar en la base de datos");
            } else {
                Toast.makeText(this, "Propuesta creada exitosamente", Toast.LENGTH_SHORT).show();

                // Enviar notificación
                Intent notificationIntent = new Intent(this, DelayedMessageService.class);
                notificationIntent.putExtra(DelayedMessageService.EXTRA_MESSAGE, getResources().getString(R.string.response));
                startService(notificationIntent);
            }
        } catch (Exception e) {
            System.out.println("CreationProposal " + "Error al crear la propuesta");
            Toast.makeText(this, "Error al crear la propuesta", Toast.LENGTH_SHORT).show();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
}