package com.example.registrodeusuario;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Register_user extends AppCompatActivity {

    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        dbHelper = new UserDatabaseHelper(this);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText emailEditText = findViewById(R.id.email);
        final EditText firstNameEditText = findViewById(R.id.first_name);
        final EditText lastNameEditText = findViewById(R.id.last_name);
        final EditText passwordEditText = findViewById(R.id.password);
        final EditText passwordConfirmEditText = findViewById(R.id.password_confirm);
        Button registerButton = findViewById(R.id.button3);

        // Validación de que las contraseñas coincidan mientras el usuario escribe
        passwordConfirmEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Verificar si las contraseñas coinciden
                String password = passwordEditText.getText().toString();
                String confirmPassword = s.toString();

                if (!password.equals(confirmPassword)) {
                    passwordConfirmEditText.setError("Las contraseñas no coinciden");
                } else {
                    passwordConfirmEditText.setError(null); // Remover el error si coinciden
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

        // Validación de campos vacíos
        if (username.isEmpty() || email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación del formato del correo electrónico
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "El correo electrónico no es válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación del formato del nombre de usuario (solo letras y números)
        if (!username.matches("[a-zA-Z0-9]+")) {
            Toast.makeText(this, "El nombre de usuario solo debe contener letras y números", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación de la longitud del nombre de usuario
        if (username.length() < 4) {
            Toast.makeText(this, "El nombre de usuario debe tener al menos 4 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación de contraseñas
        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si el usuario o el correo ya existen en la base de datos
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {UserDatabaseHelper.COLUMN_USERNAME};
        String selection = UserDatabaseHelper.COLUMN_USERNAME + " = ? OR " + UserDatabaseHelper.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {username, email};

        Cursor cursor = db.query(
                UserDatabaseHelper.TABLE_USERS,   // La tabla a consultar
                projection,                       // Las columnas a devolver
                selection,                        // Las columnas para la cláusula WHERE
                selectionArgs,                    // Los valores para la cláusula WHERE
                null,                             // No agrupar las filas
                null,                             // No filtrar por grupos de filas
                null                              // El orden
        );

        if (cursor.moveToFirst()) {
            Toast.makeText(this, "El nombre de usuario o el correo ya están registrados", Toast.LENGTH_SHORT).show();
            cursor.close();
            return;
        }
        cursor.close();

        // Si todas las validaciones pasan, insertar en la base de datos
        try {
            ContentValues values = new ContentValues();
            values.put(UserDatabaseHelper.COLUMN_USERNAME, username);
            values.put(UserDatabaseHelper.COLUMN_EMAIL, email);
            values.put(UserDatabaseHelper.COLUMN_FIRST_NAME, firstName);
            values.put(UserDatabaseHelper.COLUMN_LAST_NAME, lastName);
            values.put(UserDatabaseHelper.COLUMN_PASSWORD, password);
            values.put(UserDatabaseHelper.COLUMN_ROLE, "Ciudadano"); // Establecer el rol por defecto

            long newRowId = db.insert(UserDatabaseHelper.TABLE_USERS, null, values);

            if (newRowId == -1) {
                Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al acceder a la base de datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            db.close();  // Asegúrate de cerrar la base de datos
        }
    }
}
