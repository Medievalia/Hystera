package com.example.myapplication.navegabilidade;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LinhaDoTempo extends AppCompatActivity {
    FirebaseFirestore db;
    String usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linha_tempo);

        db = FirebaseFirestore.getInstance();
        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ImageButton btnPerfil = findViewById(R.id.button_perfil);
        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LinhaDoTempo.this, Perfil.class);
                startActivity(intent);
            }
        });

        // Chamar método para buscar e calcular a próxima menstruação
        buscarDataUltimaMenstruacao();
    }

    private void buscarDataUltimaMenstruacao() {
        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String ultimaMenstruacao = documentSnapshot.getString("UltimaMenstruacao");
                            int diasDoCiclo = documentSnapshot.getLong("diasCiclo").intValue();
                            String stringDiasDoCiclo = Integer.toString(diasDoCiclo);

                            String proximaMenstruacao = calcularProximaMenstruacao(ultimaMenstruacao, diasDoCiclo);
                            String today = calcularDataDeHoje();

                            TextView textViewProximaMenstruacao = findViewById(R.id.proximaMenst);
                            TextView textViewDiasCiclo = findViewById(R.id.mediaCiclo);

                            String dataProximaMenstruacao = calcularProximaMenstruacao(ultimaMenstruacao, diasDoCiclo);

                            textViewProximaMenstruacao.setText("Próxima: " + dataProximaMenstruacao);
                            textViewDiasCiclo.setText(stringDiasDoCiclo + " dias");

                            if (proximaMenstruacao.compareTo(today) < 0) {
                                atualizarDataUltimaMenstruacao(proximaMenstruacao);
                            }
                        } else {
                            Log.d("TAG", "Documento não encontrado");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "Erro ao buscar dados", e);
                    }
                });
    }

    private void atualizarDataUltimaMenstruacao(String novaData) {
        Map<String, Object> dadosParaAtualizar = new HashMap<>();
        dadosParaAtualizar.put("UltimaMenstruacao", novaData);

        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);

        documentReference.update(dadosParaAtualizar)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        buscarDataUltimaMenstruacao();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "Erro ao atualizar a última menstruação", e);
                    }
                });
    }

    private String calcularProximaMenstruacao(String ultimaMenstruacao, int diasDoCiclo) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        try {
            Date dataUltimaMenstruacao = dateFormat.parse(ultimaMenstruacao);

            calendar.setTime(dataUltimaMenstruacao);
            calendar.add(Calendar.DAY_OF_MONTH, diasDoCiclo);

            return dateFormat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "Erro ao calcular a data";
        }
    }

    private String calcularDataDeHoje() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        Date dataDeHoje = calendar.getTime();
        return dateFormat.format(dataDeHoje);
    }
}
