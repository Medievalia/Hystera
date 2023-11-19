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
                Intent intent = new Intent(Anotacoes.this, Anotacoes1.class);
                startActivity(intent);
            }
        });


        nota2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Anotacoes.this, Anotacoes1.class);
                startActivity(intent);
            }
        });

        nota3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Anotacoes.this, Anotacoes1.class);
                startActivity(intent);
            }
        });

        nota4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Anotacoes.this, Anotacoes1.class);
                startActivity(intent);
            }
        });

        nota5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Anotacoes.this, Anotacoes1.class);
                startActivity(intent);
            }
        });

        nota6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Anotacoes.this, Anotacoes1.class);
                startActivity(intent);
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
                    notas1.setText(documentSnapshot.getString("anotacao"));
                    notas2.setText(documentSnapshot.getString("anotacao2"));
                }
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
}