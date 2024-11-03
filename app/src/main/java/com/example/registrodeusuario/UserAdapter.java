package com.example.registrodeusuario;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> users;
    private final OnUserRoleChangeListener roleChangeListener;
    private final OnUserDeleteListener deleteListener;

    public UserAdapter(List<User> users, OnUserRoleChangeListener roleChangeListener, OnUserDeleteListener deleteListener) {
        this.users = users;
        this.roleChangeListener = roleChangeListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewUsername;
        private final TextView textViewEmail;
        private final Spinner spinnerRole;
        private final Button buttonUpdateRole;
        private final Button buttonDeleteUser;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            spinnerRole = itemView.findViewById(R.id.spinnerRole);
            buttonUpdateRole = itemView.findViewById(R.id.buttonUpdateRole);
            buttonDeleteUser = itemView.findViewById(R.id.buttonDeleteUser);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(itemView.getContext(),
                    R.array.roles_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerRole.setAdapter(adapter);
        }

        public void bind(User user) {
            textViewUsername.setText(user.getUsername());
            textViewEmail.setText(user.getEmail());

            if (user.getRole().equals("Admin")) {
                spinnerRole.setSelection(0);
            } else {
                spinnerRole.setSelection(1);
            }

            buttonUpdateRole.setOnClickListener(v -> {
                String newRole = spinnerRole.getSelectedItem().toString();
                roleChangeListener.onUserRoleChange(user.getId(), newRole);
            });

            buttonDeleteUser.setOnClickListener(v -> deleteListener.onUserDelete(user.getId(), user.getEmail()));
        }
    }

    interface OnUserRoleChangeListener {
        void onUserRoleChange(int userId, String newRole);
    }

    interface OnUserDeleteListener {
        void onUserDelete(int userId, String userEmail);
    }
}