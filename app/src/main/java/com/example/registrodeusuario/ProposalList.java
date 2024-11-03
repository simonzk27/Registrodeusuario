package com.example.registrodeusuario;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProposalList extends AppCompatActivity {

    private SimpleCursorAdapter adapter;
    private UserDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private String role;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proposal_list);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        role = intent.getStringExtra("role");

        ListView listView = findViewById(R.id.proposalListView);
        SearchView searchView = findViewById(R.id.searchView);

        dbHelper = new UserDatabaseHelper(this);
        db = dbHelper.getReadableDatabase();

        loadProposals(null);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String proposalId = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_PROPOSAL_ID));
                String proposalTitle = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_PROPOSAL_TITLE));
                String proposalDescription = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_PROPOSAL_DESCRIPTION));
                String authorFirstName = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_FIRST_NAME));
                String authorLastName = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_LAST_NAME));
                String proposalDuration = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_PROPOSAL_DURATION));
                String proposalLocality = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_PROPOSAL_LOCALITY));
                String proposalUsername = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_PROPOSAL_USERNAME));

                Intent intent = new Intent(ProposalList.this, ProposalDetailActivity.class);
                intent.putExtra("proposalId", proposalId);
                intent.putExtra("title", proposalTitle);
                intent.putExtra("description", proposalDescription);
                intent.putExtra("authorFirstName", authorFirstName);
                intent.putExtra("authorLastName", authorLastName);
                intent.putExtra("duration", proposalDuration);
                intent.putExtra("locality", proposalLocality);
                intent.putExtra("role", role);
                intent.putExtra("username", username);
                intent.putExtra("proposalUsername", proposalUsername);
                startActivityForResult(intent, 1); // Use requestCode 1
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String proposalId = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_PROPOSAL_ID));
                String proposalUsername = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_PROPOSAL_USERNAME));

                if (proposalUsername.equals(username)) {
                    showDeleteConfirmationDialog(proposalId);
                } else {
                    Toast.makeText(ProposalList.this, "No tienes permiso para eliminar esta propuesta", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadProposals(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadProposals(newText);
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Reload proposals to reflect changes
            loadProposals(null);
        }
    }

    private void loadProposals(String query) {
        Cursor cursor;
        String selection = UserDatabaseHelper.COLUMN_PROPOSAL_STATUS + " = 1";
        String[] selectionArgs = null;

        if (query != null && !query.isEmpty()) {
            selection += " AND " + UserDatabaseHelper.COLUMN_PROPOSAL_TITLE + " LIKE ?";
            selectionArgs = new String[]{"%" + query + "%"};
        }

        cursor = db.query(
                UserDatabaseHelper.TABLE_PROPOSALS,
                null,
                selection,
                selectionArgs,
                null, null, null
        );

        if (cursor != null && cursor.getCount() > 0) {
            String[] from = {UserDatabaseHelper.COLUMN_PROPOSAL_TITLE};
            int[] to = {android.R.id.text1};

            if (adapter == null) {
                adapter = new SimpleCursorAdapter(
                        this, android.R.layout.simple_list_item_1, cursor, from, to, 0
                );
                ListView listView = findViewById(R.id.proposalListView);
                listView.setAdapter(adapter);
            } else {
                adapter.changeCursor(cursor);
            }
        } else {
            Toast.makeText(this, "No hay propuestas disponibles", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog(String proposalId) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Propuesta")
                .setMessage("¿Estás seguro de que deseas eliminar esta propuesta?")
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteProposal(proposalId);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deleteProposal(String proposalId) {
        Cursor cursor = dbHelper.getProposalById(proposalId);
        if (cursor != null && cursor.moveToFirst()) {
            String proposalUsername = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_PROPOSAL_USERNAME));
            if (proposalUsername.equals(username)) {
                boolean isDeleted = dbHelper.deleteProposal(proposalId);
                if (isDeleted) {
                    Toast.makeText(this, "Propuesta eliminada", Toast.LENGTH_SHORT).show();
                    loadProposals(null); // Recargar la lista de propuestas
                } else {
                    Toast.makeText(this, "Error al eliminar la propuesta", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No tienes permiso para eliminar esta propuesta", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}