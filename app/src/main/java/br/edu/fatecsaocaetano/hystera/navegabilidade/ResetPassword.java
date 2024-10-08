package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import br.edu.fatecsaocaetano.hystera.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private String tag = "ResetPasswordClass";
    private EditText email;
    private Button btn_enviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueceu_senha1);

        iniciarComponentes();

        btn_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword(v);
            }
        });
    }

    private void resetPassword(View v) {
        String txtemail = email.getText().toString();

        if (txtemail.isEmpty()) {
            Snackbar snackbar = Snackbar.make(v, "Preencha com seu email!", Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(Color.WHITE);
            snackbar.setTextColor(Color.BLACK);
            snackbar.show();
        } else {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.sendPasswordResetEmail(txtemail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Email de redefinição de senha enviado com sucesso
                                Intent intent = new Intent(ResetPassword.this, PasswordRedefined.class);
                                Log.i(tag, "Email de redefinição de senha enviado com sucesso ao e-mail: " + txtemail);
                                startActivity(intent);
                                finish();
                            } else {
                                // Falha ao enviar o email de redefinição de senha
                                Snackbar errorSnackbar = Snackbar.make(v, "Falha ao enviar email para redefinição de senha.", Snackbar.LENGTH_SHORT);
                                errorSnackbar.setBackgroundTint(Color.WHITE);
                                errorSnackbar.setTextColor(Color.BLACK);
                                errorSnackbar.show();
                                Log.e(tag, "Erro ao enviar email para redefinição de senha.", task.getException());
                            }
                        }
                    });
        }
    }

    private void iniciarComponentes() {
        email = findViewById(R.id.email);
        btn_enviar = findViewById(R.id.btn_enviar);
    }
}