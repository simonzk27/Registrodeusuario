package com.example.registrodeusuario;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro); // Asegúrate de que este sea el nombre correcto del archivo XML

        dbHelper = new UserDatabaseHelper(this);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText emailEditText = findViewById(R.id.email);
        final EditText firstNameEditText = findViewById(R.id.first_name);
        final EditText lastNameEditText = findViewById(R.id.last_name);
        final EditText passwordEditText = findViewById(R.id.password);
        final EditText passwordConfirmEditText = findViewById(R.id.password_confirm);
        Button registerButton = findViewById(R.id.button3);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });
    }

    private void saveUserData() {
        String username = ((EditText) findViewById(R.id.username)).getText().toString();
        String email = ((EditText) findViewById(R.id.email)).getText().toString();
        String firstName = ((EditText) findViewById(R.id.first_name)).getText().toString();
        String lastName = ((EditText) findViewById(R.id.last_name)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();
        String passwordConfirm = ((EditText) findViewById(R.id.password_confirm)).getText().toString();

        // Validación de campos
        if (username.isEmpty() || email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación del correo electrónico
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "El correo electrónico no es válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación de contraseñas
        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación de la longitud de la contraseña
        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(UserDatabaseHelper.COLUMN_USERNAME, username);
            values.put(UserDatabaseHelper.COLUMN_EMAIL, email);
            values.put(UserDatabaseHelper.COLUMN_FIRST_NAME, firstName);
            values.put(UserDatabaseHelper.COLUMN_LAST_NAME, lastName);
            values.put(UserDatabaseHelper.COLUMN_PASSWORD, password);

            long newRowId = db.insert(UserDatabaseHelper.TABLE_USERS, null, values);

            if (newRowId == -1) {
                Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al acceder a la base de datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
