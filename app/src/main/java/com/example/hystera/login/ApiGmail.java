//package com.example.hystera.login;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Button;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class ApiGmail extends AppCompatActivity {
//    private static final int RC_SIGN_IN = 9001; // Código de solicitação para o login do Google
//    private GoogleSignInClient mGoogleSignInClient;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        // Configurar as opções de login do Google
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail() // Solicitar acesso ao endereço de e-mail do usuário
//                .build();
//
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//
//        // Referenciar o botão de login do Google
//        Button signInButton = findViewById(R.id.gmail_button);
//
//        // Configurar um ouvinte de clique no botão de login
//        signInButton.setOnClickListener(view -> signIn());
//    }
//
//    // Iniciar o fluxo de login do Google
//    private void signIn() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivity(signInIntent);
//    }
//
//    // Tratar o resultado do login do Google
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                // O login foi bem-sucedido, você pode usar a conta do Google do usuário aqui
//            } catch (ApiException e) {
//                // O login falhou, você pode tratar os erros aqui
//            }
//        }
//    }
//}
