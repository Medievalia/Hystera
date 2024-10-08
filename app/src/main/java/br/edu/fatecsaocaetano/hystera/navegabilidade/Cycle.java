package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.util.Log;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Cycle {

    private final String tag = "CycleClass";
    private final String userID;
    private final String id;
    private final Timestamp startDate;
    private final Timestamp endDate;
    private final int duration;
    private final boolean natural;
    private final int bleeding;
    private final Timestamp ovulation;
    private final Map<String, Map<String, Timestamp>> fases = new HashMap<>();

    public Cycle(Timestamp startDate, int duration, boolean natural, int bleeding, String userID) {
        this.startDate = startDate;
        this.duration = duration;
        this.natural = natural;
        this.bleeding = bleeding;
        this.userID = userID;
        this.id = gerarIdCiclo();

        this.endDate = calcularData(startDate, duration - 1);
        this.ovulation = calcularOvulacao();

        adicionarFase("Menstrual", startDate, calcularData(startDate, bleeding - 1));
        adicionarFase("Folicular", calcularData(startDate, bleeding), calcularData(ovulation, -1));
        adicionarFase("Lutea", calcularData(ovulation, 1), endDate);
    }

    private Timestamp calcularData(Timestamp data, int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data.toDate());
        calendar.add(Calendar.DAY_OF_MONTH, dias);
        return new Timestamp(calendar.getTime());
    }

    private Timestamp calcularOvulacao() {
        return calcularData(endDate, -14);
    }

    private void adicionarFase(String nomeFase, Timestamp inicio, Timestamp fim) {
        Map<String, Timestamp> fase = new HashMap<>();
        fase.put("inicio", inicio);
        fase.put("fim", fim);
        fases.put(nomeFase, fase);
    }

    private String gerarIdCiclo() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMyyyy", Locale.getDefault());
        return sdf.format(startDate.toDate());
    }

    public void salvarCicloNoFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference cycleRef = db.collection("Usuarios").document(userID)
                .collection("Ciclos").document(id);

        cycleRef.set(this)
                .addOnSuccessListener(aVoid -> Log.i(tag, "Ciclo salvo com sucesso! " + userID))
                .addOnFailureListener(e -> Log.e(tag,"Erro ao salvar ciclo. " + e));
    }

    public Cycle simularProximoCiclo(Cycle cicloAtual) {
        Timestamp proximoInicio = calcularData(cicloAtual.getEndDate(), 1);

        return new Cycle(proximoInicio, cicloAtual.getDuration(), cicloAtual.isNatural(), cicloAtual.getBleeding(), cicloAtual.getUserID());
    }

    public void calcularMediaDuracaoCiclos(String userID) {
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
                            double mediaDuracao = (double) somaDuracoes / quantidadeCiclos;
                            Log.i(tag, "Média da duração dos ciclos: " + mediaDuracao);
                        } else {
                            Log.e(tag, "Nenhum ciclo encontrado com duração.");
                        }
                    } else {
                        Log.e(tag, "Nenhum ciclo encontrado para o usuário.");
                    }
                })
                .addOnFailureListener(e -> Log.e("CycleClass", "Erro ao buscar ciclos.", e));
    }

    public Timestamp getStartDate() { return startDate; }
    public Timestamp getEndDate() { return endDate; }
    public int getDuration() { return duration; }
    public boolean isNatural() { return natural; }
    public int getBleeding() { return bleeding; }
    public Timestamp getOvulation() { return ovulation; }
    public String getUserID() { return userID; }
    public String getId() { return id; }
    public Map<String, Map<String, Timestamp>> getFases() {
        return fases;
    }
}
