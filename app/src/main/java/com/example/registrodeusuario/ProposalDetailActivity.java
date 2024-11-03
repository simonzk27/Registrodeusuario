package com.example.registrodeusuario;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProposalDetailActivity extends AppCompatActivity {

    private EditText titleTextView;
    private EditText descriptionTextView;
    private TextView authorTextView;
    private TextView localityTextView;
    private TextView durationTextView;
    private Spinner localitySpinner;
    private Spinner durationSpinner;
    private Button editProposalButton;
    private Button doneButton;
    private SQLiteDatabase db;
    private String proposalId;
    private Button voteButton;
    private UserDatabaseHelper dbHelper;
    private String username;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proposal_detail);

        // Initialize voteButton
        voteButton = findViewById(R.id.voteButton);

        // Initialize dbHelper
        dbHelper = new UserDatabaseHelper(this);

        // Initialize views
        titleTextView = findViewById(R.id.proposalTitle);
        descriptionTextView = findViewById(R.id.proposalDescription);
        authorTextView = findViewById(R.id.proposalAuthor);
        localityTextView = findViewById(R.id.localityTextView);
        durationTextView = findViewById(R.id.durationTextView);
        localitySpinner = findViewById(R.id.localitySpinner);
        durationSpinner = findViewById(R.id.proposalDurationSpinner);
        editProposalButton = findViewById(R.id.editProposalButton);
        doneButton = findViewById(R.id.doneButton);
        voteButton = findViewById(R.id.voteButton);

        // Get data from intent
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String authorFirstName = getIntent().getStringExtra("authorFirstName");
        String authorLastName = getIntent().getStringExtra("authorLastName");
        String duration = getIntent().getStringExtra("duration");
        String locality = getIntent().getStringExtra("locality");
        String role = getIntent().getStringExtra("role");
        username = getIntent().getStringExtra("username");
        String proposalUsername = getIntent().getStringExtra("proposalUsername");
        proposalId = getIntent().getStringExtra("proposalId");

        // Set views with data
        titleTextView.setText(title);
        descriptionTextView.setText(description);
        authorTextView.setText(authorFirstName + " " + authorLastName);
        localityTextView.setText(locality);
        durationTextView.setText(duration);

        // Set adapters for spinners
        ArrayAdapter<CharSequence> localityAdapter = ArrayAdapter.createFromResource(this,
                R.array.locality_array, android.R.layout.simple_spinner_item);
        localityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        localitySpinner.setAdapter(localityAdapter);
        localitySpinner.setSelection(localityAdapter.getPosition(locality));

        ArrayAdapter<CharSequence> durationAdapter = ArrayAdapter.createFromResource(this,
                R.array.proposal_duration_array, android.R.layout.simple_spinner_item);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durationSpinner.setAdapter(durationAdapter);
        durationSpinner.setSelection(durationAdapter.getPosition(duration));

        // Initialize database
        db = dbHelper.getWritableDatabase();

        // Check proposal status
        Cursor cursor = db.query(
                UserDatabaseHelper.TABLE_PROPOSALS,
                new String[]{UserDatabaseHelper.COLUMN_PROPOSAL_STATUS},
                UserDatabaseHelper.COLUMN_PROPOSAL_ID + " = ?",
                new String[]{proposalId},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int proposalStatus = cursor.getInt(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_PROPOSAL_STATUS));
            cursor.close();

            if (proposalStatus == 1) {
                // Show edit button if user is the author and has admin role
                if (username != null && username.equals(proposalUsername) && "Admin".equals(role)) {
                    editProposalButton.setVisibility(View.VISIBLE);
                } else {
                    editProposalButton.setVisibility(View.GONE);
                }

                if (dbHelper.hasUserVoted(username)) {
                    voteButton.setVisibility(View.GONE);
                } else {
                    voteButton.setVisibility(View.VISIBLE);
                }
            } else {
                editProposalButton.setVisibility(View.GONE);
                voteButton.setVisibility(View.GONE);
            }
        } else {
            if (cursor != null) {
                cursor.close();
            }
            editProposalButton.setVisibility(View.GONE);
            voteButton.setVisibility(View.GONE);
        }

        // Set listeners for buttons
        editProposalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEditing(true);
                doneButton.setVisibility(View.VISIBLE);
                editProposalButton.setVisibility(View.GONE);
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
                enableEditing(false);
                doneButton.setVisibility(View.GONE);
                editProposalButton.setVisibility(View.VISIBLE);

                // Set result to indicate changes were made
                setResult(RESULT_OK);
                finish();
            }
        });

        voteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteForProposal();
            }
        });
    }

    private void enableEditing(boolean enable) {
        titleTextView.setFocusable(enable);
        titleTextView.setFocusableInTouchMode(enable);
        descriptionTextView.setFocusable(enable);
        descriptionTextView.setFocusableInTouchMode(enable);
        localityTextView.setVisibility(enable ? View.GONE : View.VISIBLE);
        localitySpinner.setVisibility(enable ? View.VISIBLE : View.GONE);
        localitySpinner.setEnabled(enable);
        durationTextView.setVisibility(enable ? View.GONE : View.VISIBLE);
        durationSpinner.setVisibility(enable ? View.VISIBLE : View.GONE);
        durationSpinner.setEnabled(enable);
    }

    private void saveChanges() {
        ContentValues values = new ContentValues();
        values.put(UserDatabaseHelper.COLUMN_PROPOSAL_TITLE, titleTextView.getText().toString());
        values.put(UserDatabaseHelper.COLUMN_PROPOSAL_DESCRIPTION, descriptionTextView.getText().toString());
        values.put(UserDatabaseHelper.COLUMN_PROPOSAL_LOCALITY, localitySpinner.getSelectedItem().toString());
        values.put(UserDatabaseHelper.COLUMN_PROPOSAL_DURATION, durationSpinner.getSelectedItem().toString());

        boolean success = dbHelper.updateProposal(proposalId, values);

        if (success) {
            Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error saving changes", Toast.LENGTH_SHORT).show();
        }

        // Update the existing record in the proposals table
        int rowsAffected = db.update(
                UserDatabaseHelper.TABLE_PROPOSALS,
                values,
                UserDatabaseHelper.COLUMN_PROPOSAL_ID + " = ?",
                new String[]{proposalId}
        );

        if (rowsAffected > 0) {
            Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error saving changes", Toast.LENGTH_SHORT).show();
        }
    }

    private void voteForProposal() {
        // Check if the user has already voted
        if (dbHelper.hasUserVoted(username)) {
            Toast.makeText(this, "You have already voted for this proposal", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current votos value
        Cursor cursor = db.query(
                UserDatabaseHelper.TABLE_PROPOSALS,
                new String[]{UserDatabaseHelper.COLUMN_PROPOSAL_VOTOS},
                UserDatabaseHelper.COLUMN_PROPOSAL_ID + " = ?",
                new String[]{proposalId},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int currentVotos = cursor.getInt(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_PROPOSAL_VOTOS));
            cursor.close();

            // Increment the votos value by 1
            ContentValues values = new ContentValues();
            values.put(UserDatabaseHelper.COLUMN_PROPOSAL_VOTOS, currentVotos + 1);

            // Update the value in the database
            int rowsAffected = db.update(
                    UserDatabaseHelper.TABLE_PROPOSALS,
                    values,
                    UserDatabaseHelper.COLUMN_PROPOSAL_ID + " = ?",
                    new String[]{proposalId}
            );

            if (rowsAffected > 0) {
                Toast.makeText(this, "Vote registered successfully", Toast.LENGTH_SHORT).show();
                dbHelper.saveUserVote(username);
                voteButton.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "Error registering vote", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (cursor != null) {
                cursor.close();
            }
            Toast.makeText(this, "Error getting current votos", Toast.LENGTH_SHORT).show();
        }
    }
}