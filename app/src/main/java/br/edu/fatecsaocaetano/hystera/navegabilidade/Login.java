package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import br.edu.fatecsaocaetano.hystera.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login extends AppCompatActivity {

    private String tag = "LoginClass";
    private EditText editTextEmail, editTextSenha;
    private Button btn_seguinte;
    private Button esqueceu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        iniciarComponentes();

        esqueceu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        btn_seguinte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String senha = editTextSenha.getText().toString();

                if (email.isEmpty() || senha.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, "Preencha todos os campos!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                } else {
                    autenticarUsuario();
                }
            }
        });
    }

    private void resetPassword() {
        Intent intent = new Intent(Login.this, ResetPassword.class);
        startActivity(intent);
        finish();
    }

    private void autenticarUsuario(){
        String email = editTextEmail.getText().toString();
        String senha = editTextSenha.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    telaPrincipal();
                    Log.i(tag, "Autenticação realizada com sucesso! " + email);
                } else {
                    Toast.makeText(Login.this, "Falha na autenticação: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                   Log.e(tag, "Falha na autenticação para o e-mail: " + email);
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if(usuarioAtual != null) {
            telaPrincipal();
        }
    }

    private void telaPrincipal(){
        Intent intent = new Intent(Login.this, LinhaDoTempo.class);
        startActivity(intent);
        finish();
    }
    private void iniciarComponentes() {
        esqueceu = findViewById(R.id.esqueceu);
        editTextEmail = findViewById(R.id.email);
        editTextSenha = findViewById(R.id.editTextSenha);
        btn_seguinte = findViewById(R.id.btn_seguinte);
    }
}

