package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.edu.fatecsaocaetano.hystera.R;

public class MainActivity extends AppCompatActivity {

    private final String tag = "MainActivityClass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            Intent intent = new Intent(MainActivity.this, TimeLine.class);
            startActivity(intent);
            Log.i(tag, "Usuário já autenticado, inicializando tela de linha do tempo!");
            finish();
        } else {
            btnTenhoUmaConta();
            btnComeceAgora();
        }
    }

    private void btnTenhoUmaConta() {
        AppCompatButton tenhoUmaConta = findViewById(R.id.tenho_uma_conta);
        tenhoUmaConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void btnComeceAgora() {
        AppCompatButton comeceAgora = findViewById(R.id.comece_agora);
        comeceAgora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void login() {
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
    }

    private void createAccount() {
        Intent intent = new Intent(MainActivity.this, CreateAccount.class);
        startActivity(intent);
    }
}
