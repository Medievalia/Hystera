package com.example.myapplication.navegabilidade;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class Esqueceu1 extends AppCompatActivity {

    private EditText email;
    private Button btn_enviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueceu_senha1);

        IniciarComponentes();

        btn_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtemail = email.getText().toString();

                if (txtemail.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, "Escreva seu email", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                } else {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String emailAddress = txtemail;

                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Email de redefinição de senha enviado com sucesso
                                        Intent intent = new Intent(Esqueceu1.this, Esqueceu2.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Falha ao enviar o email de redefinição de senha
                                        Snackbar errorSnackbar = Snackbar.make(v, "Falha ao enviar email para redefinição de senha.", Snackbar.LENGTH_SHORT);
                                        errorSnackbar.setBackgroundTint(Color.WHITE);
                                        errorSnackbar.setTextColor(Color.BLACK);
                                        errorSnackbar.show();
                                        Log.e("TAG", "Erro ao enviar email para redefinição de senha.", task.getException());
                                    }
                                }
                            });
                }
            }
        });

    }
    private void IniciarComponentes() {
        email = findViewById(R.id.email);
        btn_enviar = findViewById(R.id.btn_enviar);
    }
}
