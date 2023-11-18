package com.example.myapplication.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;

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

public class Cadastrar4 extends AppCompatActivity {
    int metodo;
    String usuarioId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_4);

        ImageButton botao1 = findViewById(R.id.botao1);
        ImageButton botao2 = findViewById(R.id.botao2);
        ImageButton botao3 = findViewById(R.id.botao3);
        ImageButton botao4 = findViewById(R.id.botao4);
        ImageButton botao5 = findViewById(R.id.botao5);
        AppCompatButton botao6 = findViewById(R.id.botao6);
        AppCompatButton btn_n_utilizo = findViewById(R.id.btn_n_utilizo);
        ImageButton voltar = findViewById(R.id.back_button);

        botao1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodo = 1;//Camisinha
                SalvarDados(metodo);
            }
        });
        botao2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodo = 2;//Pilula
                SalvarDados(metodo);
            }
        });
        botao3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodo = 3; //DIU
                SalvarDados(metodo);
            }
        });
        botao4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodo = 4;//Injeção
                SalvarDados(metodo);
            }
        });
        botao5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodo = 5; //CHIP
                SalvarDados(metodo);
            }
        });
        botao6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodo = 6; //Outros
                SalvarDados(metodo);
            }
        });
        btn_n_utilizo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodo = 7;//Não utilizo
                SalvarDados(metodo);
            }
        });
    }

    private void SalvarDados(int metodo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> dadosParaAtualizar = new HashMap<>();

        dadosParaAtualizar.put("metodo", metodo);

        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);

        // Atualizar o documento com o novo campo usando merge para preservar os dados existentes
        documentReference.set(dadosParaAtualizar, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Ação de sucesso ao salvar os dados
                        Log.d("TAG", "Escolha salva com sucesso!");
                        Intent intent = new Intent(Cadastrar4.this, LinhaDoTempo.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Lidar com falhas ao salvar os dados
                        Log.e("TAG", "Erro ao salvar a escolha.", e);
                    }
                });
    }
}
