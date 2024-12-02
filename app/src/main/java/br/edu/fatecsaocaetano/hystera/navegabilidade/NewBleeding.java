package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.Calendar;

public class NewBleeding {

    private static final String tag = "NewBleedingClass";
    private String userID;
    private Cycle currentCycle;
    private int cycleAverage;

    public void validateNewCycle(String id, Context context) {
        userID = id;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Usuarios")
                .document(userID)
                .collection("Ciclos")
                .orderBy("startDate", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        currentCycle = document.toObject(Cycle.class);

                        if (currentCycle != null) {
                            handleNewBleeding(context, currentCycle);
                        } else {
                            Log.e(tag, "Ciclo atual não encontrado.");
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(tag, "Erro ao buscar ciclo atual.", e));
    }

    private void handleNewBleeding(Context context, Cycle cycle) {
        Timestamp today = new Timestamp(Calendar.getInstance().getTime());

        // Marca o ciclo como interrompido
        cycle.setEndDate(cycle.ajustarParaFimDoDia(cycle.calcularData(today, -1)));
        cycle.setDuration(cycle.calcularDuracao());

        // Define a propriedade 'interrupted' como true
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Usuarios").document(userID)
                .collection("Ciclos").document(cycle.getId())
                .update("interrupted", true)
                .addOnSuccessListener(aVoid -> {
                    Log.i(tag, "Ciclo marcado como interrompido com sucesso.");
                    cycle.setInterrupted(true);
                    cycle.salvarCicloNoFirebase();

                    // Calcular a média e criar um novo ciclo após a atualização do ciclo atual
                    calcularMediaDuracaoCiclos(userID, average -> {
                        Cycle newCycle = new Cycle(today, average, cycle.isNatural(), cycle.getBleeding(), userID);
                        newCycle.salvarCicloNoFirebase();
                        updateTimelineWithNewCycle(context);
                        Log.i(tag, "Novo ciclo criado conforme decisão do usuário ");
                    });
                })
                .addOnFailureListener(e -> Log.e(tag, "Erro ao marcar ciclo como interrompido.", e));
    }

    public void calcularMediaDuracaoCiclos(String userID, OnAverageCalculatedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Usuarios").document(userID)
                .collection("Ciclos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        int somaDuracoes = 0;
                        int quantidadeCiclos = 0;

                        for (DocumentSnapshot cicloDoc : queryDocumentSnapshots.getDocuments()) {
                            if (cicloDoc.contains("duration")) {
                                int duracao = cicloDoc.getLong("duration").intValue();
                                somaDuracoes += duracao;
                                quantidadeCiclos++;
                            }
                        }

                        if (quantidadeCiclos > 0) {
                            // Dividir como double antes de converter para int para evitar perda de precisão
                            double mediaDuracao = (double) somaDuracoes / quantidadeCiclos;
                            int mediaDuracaoInt = (int) Math.round(mediaDuracao); // Arredondar para o inteiro mais próximo
                            Log.i(tag, "Média da duração dos ciclos: " + mediaDuracaoInt);

                            db.collection("Usuarios").document(userID)
                                    .update("Cycle Average", mediaDuracaoInt) // Atualiza como inteiro
                                    .addOnSuccessListener(aVoid -> {
                                        Log.i(tag, "Média atualizada com sucesso!");
                                        listener.onAverageCalculated(mediaDuracaoInt); // Chama o listener com a média calculada
                                    })
                                    .addOnFailureListener(e -> Log.e(tag, "Erro ao atualizar a média.", e));
                        } else {
                            Log.e(tag, "Nenhum ciclo encontrado com duração.");
                        }
                    } else {
                        Log.e(tag, "Nenhum ciclo encontrado para o usuário.");
                    }
                })
                .addOnFailureListener(e -> Log.e(tag, "Erro ao buscar ciclos.", e));
    }


    // Interface para callback
    public interface OnAverageCalculatedListener {
        void onAverageCalculated(int average);
    }


    private void updateTimelineWithNewCycle(Context context) {
        // Lógica para atualizar a linha do tempo, reiniciando a atividade
        Intent intent = new Intent(context, TimeLine.class); // Usando o contexto passado
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

