package com.example.myapplication.navegabilidade;

import android.os.Bundle;
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

public class Perfil extends AppCompatActivity {

    private TextView nomeUsuario, senhaUsuario, emailUsuario, numeroUsuario, metodoContraUsuario;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String usuarioID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        iniciarComponente();
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
        senhaUsuario = findViewById(R.id.txt_senha);
        emailUsuario = findViewById(R.id.txt_email);
        numeroUsuario = findViewById(R.id.txt_num);
        metodoContraUsuario = findViewById(R.id.txt_metodo);
    }
}
