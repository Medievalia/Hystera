package com.example.myapplication.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Anotacoes1 extends AppCompatActivity {
    private EditText anotacaoEditText;
    String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anotacao1);

        anotacaoEditText = findViewById(R.id.anotacao);

        AppCompatButton salvar = findViewById(R.id.btn_salvar);
        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvarAnotacao();
            }
        });
    }

    private void salvarAnotacao() {
        String anota = anotacaoEditText.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("Usuarios").document(usuarioID);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Verifica se o campo anotacao já existe
                    if (documentSnapshot.contains("anotacao")) {
                        String anotacaoExistente = documentSnapshot.getString("anotacao");

                        // Se o campo anotacao já tem conteúdo, crie um novo campo anotacao2
                        if (!anotacaoExistente.isEmpty()) {
                            Map<String, Object> dadosParaAtualizar = new HashMap<>();
                            dadosParaAtualizar.put("anotacao2", anota);

                            documentReference.set(dadosParaAtualizar, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Ação de sucesso ao salvar os dados
                                            Log.d("TAG", "Nova anotação salva com sucesso!");

                                            Intent intent = new Intent(Anotacoes1.this, Anotacoes.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("db error", "Erro ao salvar a nova anotação" + e.toString());
                                        }
                                    });
                        } else {
                            // Se o campo anotacao não tem conteúdo, salve nele diretamente
                            Map<String, Object> dadosParaAtualizar = new HashMap<>();
                            dadosParaAtualizar.put("anotacao", anota);

                            documentReference.set(dadosParaAtualizar, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Ação de sucesso ao salvar os dados
                                            Log.d("TAG", "Anotação salva com sucesso!");

                                            Intent intent = new Intent(Anotacoes1.this, Anotacoes.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("db error", "Erro ao salvar a anotação" + e.toString());
                                        }
                                    });
                        }
                    }
                }
            }
        });
    }
}
