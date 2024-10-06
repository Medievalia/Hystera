package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Notificacoes extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView notifica, notifica2, ativar;
    String diasProxima;
    private boolean notificationsEnabled = true;
    String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacoes);

        final Switch notificationSwitch = findViewById(R.id.notificationSwitch);
        notificationSwitch.setChecked(notificationsEnabled);
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notificationsEnabled = isChecked;
                if (notificationsEnabled == false) {
                    notifica = findViewById(R.id.notifica);
                    notifica.setText("");
                    notifica2 = findViewById(R.id.notifica2);
                    notifica2.setText("");
                    ativar = findViewById(R.id.buttonShowNotification);
                    ativar.setText("Ativar Notificações");
                }
                else{
                    ativar = findViewById(R.id.buttonShowNotification);
                    ativar.setText("Desativar Notificações");
                    onStart();
                }
            }
        });

        ImageButton anota = findViewById(R.id.anota);
        anota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notificacoes.this, Anotacoes.class);
                startActivity(intent);
            }
        });

        ImageButton calendario = findViewById(R.id.calendario);
        calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notificacoes.this, Calendario.class);
                startActivity(intent);
            }
        });

        ImageButton utero = findViewById(R.id.utero);
        utero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notificacoes.this, Informacao.class);
                startActivity(intent);
            }
        });

        ImageButton menu = findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notificacoes.this, Notificacoes.class);
                startActivity(intent);
            }
        });

        ImageButton seekbar1 = findViewById(R.id.seekbar);
        seekbar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notificacoes.this, LinhaDoTempo.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        DocumentReference documentReference = db.collection("Usuarios").document(usuarioID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    diasProxima = (documentSnapshot.getString("diasProxima"));
                    String notificaProxima = "Faltam " + diasProxima + " dias para a próxima menstruação";
                    notifica = findViewById(R.id.notifica);
                    notifica.setText(notificaProxima);

                    String periodo = (documentSnapshot.getString("periodo"));
                    if (periodo.equals("Período Fértil")) {
                        notifica2 = findViewById(R.id.notifica2);
                        notifica2.setText("Você está no seu periodo fértil");
                    } else if (periodo.equals("Período Menstrual")){
                        notifica2 = findViewById(R.id.notifica2);
                        notifica2.setText("Você está no seu periodo menstrual");
                    } else if (periodo.equals("Fase Folicular")){
                        notifica2 = findViewById(R.id.notifica2);
                        notifica2.setText("Você está no seu periodo folicular");
                    } else {
                        notifica2 = findViewById(R.id.notifica2);
                        notifica2.setText("Você está no seu periodo lúteo");
                    }
                }
            }
        });
    }
}


