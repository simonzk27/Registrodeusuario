package com.example.registrodeusuario;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserList extends AppCompatActivity {

    private UserDatabaseHelper dbHelper;
    private UserAdapter userAdapter;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);

        dbHelper = new UserDatabaseHelper(this);
        currentUserEmail = getIntent().getStringExtra("username");

        RecyclerView recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new UserAdapter(new ArrayList<>(), this::onUserRoleChange, this::onUserDelete);
        recyclerView.setAdapter(userAdapter);

        loadUsers();
    }

    private void loadUsers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(UserDatabaseHelper.TABLE_USERS, null, null, null, null, null, null);

        List<User> users = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_ID));
            String username = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_USERNAME));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_EMAIL));
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_LAST_NAME));
            String role = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_ROLE));

            users.add(new User(id, username, email, firstName, lastName, role));
        }
        cursor.close();

        userAdapter.updateUsers(users);
    }

    private void onUserRoleChange(int userId, String newRole) {
        if (dbHelper.updateUserRole(userId, newRole)) {
            Toast.makeText(this, "Rol actualizado", Toast.LENGTH_SHORT).show();
            loadUsers();
        } else {
            Toast.makeText(this, "Error al actualizar el rol", Toast.LENGTH_SHORT).show();
        }
    }

    private void onUserDelete(int userId, String userEmail) {
        if (currentUserEmail.equals(userEmail)) {
            Toast.makeText(this, "No puedes eliminar tu propio usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.deleteUser(userId)) {
            Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
            loadUsers();
        } else {
            Toast.makeText(this, "Error al eliminar el usuario", Toast.LENGTH_SHORT).show();
        }
    }
}