package com.example.registrodeusuario;

import android.app.Notification;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class MainActivity extends AppCompatActivity {

    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Botón de registrarse
        Button btnRegister = findViewById(R.id.button2);

        // Al hacer clic en el botón de registro, inicia la actividad Register_user
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Register_user.class);
                startActivity(intent);
            }
        });

        dbHelper = new UserDatabaseHelper(this);

        final EditText emailEditText = findViewById(R.id.email);
        final EditText passwordEditText = findViewById(R.id.password);
        Button btnLogin = findViewById(R.id.button1);

        // Manejador de eventos al hacer clic en el botón de login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Capturar los datos de los campos al hacer clic en el botón
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Llamar a loginUser con los valores capturados
                loginUser(email, password);
            }
        });
    }

    private void loginUser(String email, String password) {
        // Validación de campos vacíos
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Por favor, ingrese su correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor, ingrese su contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación del formato del correo electrónico
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "El formato del correo electrónico no es válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si el usuario existe en la base de datos
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                UserDatabaseHelper.COLUMN_PASSWORD,
                UserDatabaseHelper.COLUMN_ROLE,
                UserDatabaseHelper.COLUMN_USERNAME,
                UserDatabaseHelper.COLUMN_FIRST_NAME,
                UserDatabaseHelper.COLUMN_LAST_NAME
        };
        String selection = UserDatabaseHelper.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};

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
            String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_PASSWORD));
            String userRole = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_ROLE));
            String username = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_USERNAME));
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_LAST_NAME));

            cursor.close();

            // Verificar la contraseña
            if (password.equals(storedPassword)) {
                // Credenciales correctas, ir a la actividad correspondiente según el rol
                Intent intent;
                if ("Admin".equals(userRole)) {
                    intent = new Intent(MainActivity.this, MenuAdmin.class);
                } else {
                    intent = new Intent(MainActivity.this, MenuUsuario.class);
                }
                intent.putExtra("username", username); // Pasar el email del usuario
                intent.putExtra("firstName", firstName); // Pasar el nombre del usuario
                intent.putExtra("lastName", lastName); // Pasar el apellido
                intent.putExtra("role", userRole); // Pasar el rol
                startActivity(intent);

            } else {
                Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
            }
        } else {
            cursor.close();
            Toast.makeText(this, "Correo electrónico no registrado", Toast.LENGTH_SHORT).show();
        }
    }
}