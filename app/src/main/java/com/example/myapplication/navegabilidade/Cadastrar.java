package com.example.myapplication.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Cadastrar extends AppCompatActivity {

    private Button monitorar;
    private Button engravidar;
    private boolean choice;
    String usuarioId;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_1);

        AppCompatButton monitorar = findViewById(R.id.monitorar);
        AppCompatButton engravidar = findViewById(R.id.engravidar);

        IniciarComponentes();

        monitorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = false;
                SalvarDados();
                Intent intent = new Intent(Cadastrar.this, Cadastrar2.class);
                startActivity(intent);
            }
        });
        engravidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = true;
                SalvarDados();
                Intent intent = new Intent(Cadastrar.this, Cadastrar2.class);
                startActivity(intent);
            }
        });
    }

    private void SalvarDados(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> dadosParaAtualizar = new HashMap<>();
        dadosParaAtualizar.put("escolha", choice);

        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);

        // Atualizar o documento com o novo campo usando merge para preservar os dados existentes
        documentReference.set(dadosParaAtualizar, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Ação de sucesso ao salvar os dados
                        Log.d("TAG", "Dados salvos com sucesso!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Lidar com falhas ao salvar os dados
                        Log.e("TAG", "Erro ao salvar os dados.", e);
                    }
                });
    }
    private void IniciarComponentes() {
        monitorar = findViewById(R.id.monitorar);
        engravidar = findViewById(R.id.engravidar);
    }
}
