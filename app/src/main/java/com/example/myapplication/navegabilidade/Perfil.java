package com.example.myapplication.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Perfil extends AppCompatActivity {

    private TextView nomeUsuario, senhaUsuario, emailUsuario, numeroUsuario, metodoContraUsuario;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String usuarioID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        iniciarComponente();

        AppCompatButton perfilEditar =  findViewById(R.id.btn_pefil_editar);
        perfilEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Perfil.this, EditarPerfil.class);
                startActivity(intent);
            }
        });

        AppCompatButton cicloEditar =  findViewById(R.id.btn_ciclo_editar);
        cicloEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Perfil.this, Cadastrar4.class);
                startActivity(intent);
            }
        });

        AppCompatButton alterarSenha =  findViewById(R.id.btn_alterar_senha);
        alterarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Perfil.this, Esqueceu1.class);
                startActivity(intent);
            }
        });

        ImageButton ajuda =  findViewById(R.id.help_button);
        ajuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Perfil.this, Ajuda.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onStart(){
        super.onStart();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        DocumentReference documentReference = db.collection("Usuarios").document(usuarioID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null){
                    nomeUsuario.setText(documentSnapshot.getString("nome"));
                    emailUsuario.setText(email);
                    numeroUsuario.setText(documentSnapshot.getString("celular"));
                    metodoContraUsuario.setText(documentSnapshot.getString("metodo"));

                }
            }
        });
    }
    private void iniciarComponente(){
        nomeUsuario = findViewById(R.id.txt_nome);
        emailUsuario = findViewById(R.id.txt_email);
        numeroUsuario = findViewById(R.id.txt_num);
        metodoContraUsuario = findViewById(R.id.txt_metodo);
    }

    public void onImageButtonClick(View view) {
        // Este método é chamado quando o ImageButton é clicado
        Intent intent = new Intent(Perfil.this, LinhaDoTempo.class);
        startActivity(intent);
    }
}
