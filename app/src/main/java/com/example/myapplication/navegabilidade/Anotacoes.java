package com.example.myapplication.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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

import org.w3c.dom.Text;


public class Anotacoes extends AppCompatActivity {
    TextView notas1,notas2,notas3,notas4,notas5,notas6;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String usuarioID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anotacao);

        TextView nota1 = findViewById(R.id.nota1);
        TextView nota2 = findViewById(R.id.nota2);
        TextView nota3 = findViewById(R.id.nota3);
        TextView nota4 = findViewById(R.id.nota4);
        TextView nota5 = findViewById(R.id.nota5);
        TextView nota6 = findViewById(R.id.nota6);
        iniciarComponente();
        nota1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirAnotacao(1);
            }
        });
        nota2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirAnotacao(2);
            }
        });
        nota3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirAnotacao(3);
            }
        });
        nota4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirAnotacao(4);
            }
        });
        nota5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirAnotacao(5);
            }
        });
        nota6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirAnotacao(6);
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("Usuarios").document(usuarioID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null){
                    notas1.setText(documentSnapshot.getString("anotacao1"));
                    notas2.setText(documentSnapshot.getString("anotacao2"));
                    notas3.setText(documentSnapshot.getString("anotacao3"));
                    notas4.setText(documentSnapshot.getString("anotacao4"));
                    notas5.setText(documentSnapshot.getString("anotacao5"));
                    notas6.setText(documentSnapshot.getString("anotacao6"));
                }
            }
        });

        //barra inferior
        ImageButton anota = findViewById(R.id.anota);
        anota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Anotacoes.this, Anotacoes.class);
                startActivity(intent);
            }
        });

        ImageButton calendario = findViewById(R.id.calendario);
        calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Anotacoes.this, Calendario.class);
                startActivity(intent);
            }
        });

        ImageButton utero = findViewById(R.id.utero);
        utero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Anotacoes.this, Informacao.class);
                startActivity(intent);
            }
        });



        ImageButton menu = findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Anotacoes.this, Notificacoes.class);
                startActivity(intent);
            }
        });

        ImageButton seekbar1 = findViewById(R.id.seekbar);
        seekbar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Anotacoes.this, LinhaDoTempo.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void iniciarComponente(){
        notas1 = findViewById(R.id.nota1);
        notas2 = findViewById(R.id.nota2);
        notas3 = findViewById(R.id.nota3);
        notas4 = findViewById(R.id.nota4);
        notas5 = findViewById(R.id.nota5);
        notas6 = findViewById(R.id.nota6);
    }
    private void abrirAnotacao(int notaId) {
        Intent intent = new Intent(Anotacoes.this, Anotacoes1.class);
        intent.putExtra("notaId", notaId);
        startActivity(intent);
    }
}
