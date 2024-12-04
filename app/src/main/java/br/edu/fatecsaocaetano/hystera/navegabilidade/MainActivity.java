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
import br.edu.fatecsaocaetano.hystera.navegabilidade.NotificationHelper;

public class MainActivity extends AppCompatActivity {

    private final String tag = "MainActivityClass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        // Verificando se o usuário está autenticado
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            // Se o usuário estiver autenticado, vai para a tela de Timeline
            Intent intent = new Intent(MainActivity.this, TimeLine.class);
            startActivity(intent);
            Log.i(tag, "Usuário já autenticado, inicializando tela de linha do tempo!");
            finish();
        } else {
            // Se o usuário não estiver autenticado, configura os botões de login e criação de conta
            btnTenhoUmaConta();
            btnComeceAgora();
        }

        // Inicializa e agenda as notificações
        scheduleNotifications();
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

    // Método para agendar as notificações
    private void scheduleNotifications() {
        NotificationHelper notificationHelper = new NotificationHelper(this);

        // Verifica se o usuário está autenticado para agendar as notificações
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userID = usuarioAtual.getUid();

            // Envia notificações
            notificationHelper.sendImmediateNotification("Aplicativo Aberto", "Você abriu o aplicativo.");
            notificationHelper.scheduleCyclePhaseNotifications(this, userID);
            notificationHelper.scheduleMedicationNotifications(userID);
        }
    }
}
