package com.example.myapplication.navegabilidade;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
        ImageButton voltar = findViewById(R.id.back_button);

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Anotacoes1.this, Anotacoes.class);
                startActivity(intent);
            }
        });


        anotacaoEditText = findViewById(R.id.anotacao);
        int notaId = getIntent().getIntExtra("notaId", 0);

        // Agora você pode usar o ID da nota para recuperar dados específicos do banco de dados
        carregarAnotacao(notaId);

        AppCompatButton salvar = findViewById(R.id.btn_salvar);
        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvarAnotacao();
            }
        });

        AppCompatButton btnExcluir = findViewById(R.id.excluir);
        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarExclusao();
            }
        });
    }

    private void salvarAnotacao() {
        String anotacao = anotacaoEditText.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("Usuarios").document(usuarioID);

        int notaId = getIntent().getIntExtra("notaId", 0);

        if (notaId > 0) {
            // Atualizar anotação existente
            String fieldName = "anotacao" + notaId;
            Map<String, Object> dadosParaAtualizar = new HashMap<>();
            dadosParaAtualizar.put(fieldName, anotacao);

            documentReference.set(dadosParaAtualizar, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG", "Anotação alterada com sucesso!");

                            Intent intent = new Intent(Anotacoes1.this, Anotacoes.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("db error", "Erro ao alterar a anotação" + e.toString());
                        }
                    });
        } else {
            // Adicionar nova anotação
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    int numAnotacoes = 0;

                    // Verifica quantas anotações existem atualmente
                    for (int i = 1; i <= 6; i++) {
                        String fieldName = "anotacao" + i;
                        if (documentSnapshot.contains(fieldName) && !documentSnapshot.getString(fieldName).isEmpty()) {
                            numAnotacoes++;
                        }
                    }

                    // Verifica se há espaço para mais anotações
                    if (numAnotacoes < 6) {
                        // Encontrar o próximo campo disponível
                        int proximoCampo = numAnotacoes + 1;
                        String fieldName = "anotacao" + proximoCampo;

                        Map<String, Object> dadosParaAtualizar = new HashMap<>();
                        dadosParaAtualizar.put(fieldName, anotacao);

                        documentReference.set(dadosParaAtualizar, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
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
                        // Se não houver espaço para mais anotações
                        Log.d("TAG", "Não é possível adicionar mais anotações. Limite atingido.");
                    }
                }
            });
        }
    }

    private void carregarAnotacao(int notaId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("Usuarios").document(usuarioID);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String fieldName = "anotacao" + notaId;
                    if (documentSnapshot.contains(fieldName)) {
                        String anotacao = documentSnapshot.getString(fieldName);
                        anotacaoEditText.setText(anotacao);
                    }
                }
            }
        });
    }

    private void confirmarExclusao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmação");
        builder.setMessage("Deseja realmente excluir esta anotação?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                excluirAnotacao();
            }
        });
        builder.setNegativeButton("Não", null);
        builder.show();
    }

    private void excluirAnotacao() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("Usuarios").document(usuarioID);
        int notaId = getIntent().getIntExtra("notaId", 0);

        // Use o ID da nota para excluir a anotação correspondente
        String fieldName = "anotacao" + notaId;

        Map<String, Object> dadosParaAtualizar = new HashMap<>();
        dadosParaAtualizar.put(fieldName, ""); // Define o campo como nulo para excluir

        documentReference.update(dadosParaAtualizar)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Ação de sucesso ao excluir a anotação
                        Log.d("TAG", "Anotação excluída com sucesso!");

                        Intent intent = new Intent(Anotacoes1.this, Anotacoes.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("db error", "Erro ao excluir a anotação" + e.toString());
                    }
                });
    }
}

