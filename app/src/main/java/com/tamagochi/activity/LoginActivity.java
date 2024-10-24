package com.tamagochi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.tamagochi.R;
import com.tamagochi.model.User;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.d("MainActivity", "Usuário não está logado, mostrando o botão Google Sign-In.");
            configureGoogleSignIn();
        } else {
            Log.d("MainActivity", "Usuário já está logado: " + currentUser.getEmail());
            String userName = currentUser.getDisplayName();
            TextView welcomeTextView = findViewById(R.id.welcome_text_view);
            welcomeTextView.setText("Bem-vindo, " + userName + "!");
        }

        findViewById(R.id.google_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Iniciando processo de login com Google.");
                signInWithGoogle();
            }
        });
    }

    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Log.d("MainActivity", "Resultado do Google Sign-In recebido.");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(Exception.class);
                Log.d("MainActivity", "Google Sign-In bem-sucedido: " + account.getEmail());
                firebaseAuthWithGoogle(account);
            } catch (Exception e) {
                Log.w("MainActivity", "Erro no Google Sign-In", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("MainActivity", "Autenticando com o Firebase usando o Google Account: " + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("MainActivity", "Autenticação com Firebase bem-sucedida.");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Log.d("MainActivity", "Usuário autenticado: " + user.getDisplayName());
                            User userObj = new User(user.getEmail(), user.getDisplayName(), user.getUid());
                            Log.d("USER DATA", userObj.toString());
                        }
                    } else {
                        Log.w("MainActivity", "Falha na autenticação com Firebase.", task.getException());
                    }
                    if (task.isSuccessful()) {
                        Log.d("LoginActivity", "Login bem-sucedido. Retornando para MainActivity.");
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish(); // Encerra LoginActivity
                    }
                });

    }


}
