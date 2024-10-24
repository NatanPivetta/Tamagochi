package com.tamagochi.activity;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tamagochi.R;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        configureGoogleSignIn();
        if (currentUser == null) {
            Log.d("MainActivity", "Usuario nao esta logado, redirecionando para LoginActivity.");
            // Usuário não está logado, redireciona para a LoginActivity
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            // finish();
        } else {
            Log.d("MainActivity", "Usuario esta logado: " + currentUser.getEmail());
            // Usuário está logado
            String userName = currentUser.getDisplayName();
            TextView welcomeTextView = findViewById(R.id.welcome_text_view);
            welcomeTextView.setText("Bem-vindo, " + userName + "!");

            Button logOutButton;
            logOutButton = findViewById(R.id.logOutButton);
            logOutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("MainActivity", "Iniciando processo de login com Google.");
                    logOutAndRevokeAccess();

                }
            });
        }
    }

    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void logOutAndRevokeAccess() {
        // Faz logout do Firebase
        FirebaseAuth.getInstance().signOut();

        // Revoga o acesso do Google para que o usuário possa escolher outra conta
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d("MainActivity", "Acesso revogado. O usuário poderá escolher outra conta.");

                // Redireciona para a LoginActivity
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish(); // Encerra a MainActivity para que o usuário não possa voltar
            } else {
                Log.w("MainActivity", "Falha ao revogar o acesso do Google.", task.getException());
            }
        });
    }
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String userName = user.getDisplayName();
            TextView welcomeTextView = findViewById(R.id.welcome_text_view);
            welcomeTextView.setText("Bem-vindo, " + userName + "!");

        }
    }

}
