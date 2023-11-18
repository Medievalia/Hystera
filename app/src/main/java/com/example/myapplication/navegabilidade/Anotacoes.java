package com.example.myapplication.navegabilidade;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Anotacoes extends AppCompatActivity {
    int nota;
    String usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anotacao);

        ImageButton nota1 = findViewById(R.id.nota1);
        ImageButton nota2 = findViewById(R.id.nota2);
        ImageButton nota3 = findViewById(R.id.nota3);
        ImageButton nota4 = findViewById(R.id.nota4);
        ImageButton nota5 = findViewById(R.id.nota5);
        ImageButton nota6 = findViewById(R.id.nota6);

        nota1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nota = 1;
                exibirDialogoDeAnotacao();
            }
        });

        nota2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nota = 2;
                exibirDialogoDeAnotacao();
            }
        });

        nota3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nota = 3;
                exibirDialogoDeAnotacao();
            }
        });

        nota4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nota = 4;
                exibirDialogoDeAnotacao();
            }
        });

        nota5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nota = 5;
                exibirDialogoDeAnotacao();
            }
        });

        nota6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nota = 6;
                exibirDialogoDeAnotacao();
            }
        });
    }

    private void exibirDialogoDeAnotacao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_anotacao, null);
        EditText editTextAnotacaoDialog = dialogView.findViewById(R.id.editTextAnotacaoDialog);

        builder.setView(dialogView)
                .setTitle("Digite sua anotação")
                .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String textoAnotacao = editTextAnotacaoDialog.getText().toString();
                        salvarAnotacao(textoAnotacao, nota);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void salvarAnotacao(String textoAnotacao, int nota) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> dadosParaAtualizar = new HashMap<>();
        dadosParaAtualizar.put("anotacao" + nota, textoAnotacao);

        DocumentReference documentReference = db.collection("anotacoes").document(usuarioId);

        documentReference.set(dadosParaAtualizar, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "Anotação salva com sucesso!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "Erro ao salvar a anotação.", e);
                    }
                });
    }
}
