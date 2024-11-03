package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.util.Log;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Cycle {

    private final String tag = "CycleClass";
    private String userID;
    private String id;
    private Timestamp startDate;
    private Timestamp endDate;
    private int duration;
    private boolean natural;
    private int bleeding;
    private Timestamp ovulation;
    private boolean interrupted;
    private final Map<String, Map<String, Timestamp>> fases = new HashMap<>();

    public Cycle() {
    }

    public Cycle(Timestamp startDate, int duration, boolean natural, int bleeding, String userID) {
        this.startDate = startDate;
        this.duration = duration;
        this.natural = natural;
        this.bleeding = bleeding;
        this.userID = userID;
        this.id = gerarIdCiclo();
        this.interrupted = false;
        this.endDate = calcularData(startDate, duration - 1);
        this.ovulation = calcularOvulacao();

        adicionarFase("Menstrual", startDate, calcularData(startDate, bleeding - 1));
        adicionarFase("Folicular", calcularData(startDate, bleeding), calcularData(ovulation, -1));
        adicionarFase("Lutea", calcularData(ovulation, 1), endDate);
        adicionarFase("Ovulacao", calcularOvulacao(), calcularOvulacao());
    }

    public Timestamp calcularData(Timestamp data, int dias) {
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
        String baseId = sdf.format(startDate.toDate());
        String timestamp = String.valueOf(startDate.getSeconds());  // Usar o timestamp de segundos para garantir unicidade
        return baseId + "-" + timestamp;
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

    public Map<String, String> obterInformacoesDoCicloAtual() {
        Calendar hoje = Calendar.getInstance();
        hoje.setTime(new Date());

        Map<String, String> informacoesCiclo = new HashMap<>();

        long diasDesdeInicio = (hoje.getTimeInMillis() - getStartDate().toDate().getTime()) / (1000 * 60 * 60 * 24);
        int diaDoCiclo = (int) diasDesdeInicio + 1;

        String faseAtual = "Sangramento";
        for (Map.Entry<String, Map<String, Timestamp>> entry : fases.entrySet()) {
            String nomeFase = entry.getKey();
            Timestamp inicioFase = entry.getValue().get("inicio");
            Timestamp fimFase = entry.getValue().get("fim");

            if (hoje.getTimeInMillis() >= inicioFase.toDate().getTime() && hoje.getTimeInMillis() <= fimFase.toDate().getTime()) {
                faseAtual = nomeFase;
                break;
            }
        }
        informacoesCiclo.put("faseAtual", faseAtual);
        informacoesCiclo.put("diaDoCiclo", String.valueOf(diaDoCiclo));

        return informacoesCiclo;
    }

    public Map<String, Integer> calcularDiasRestantes() {

        Calendar hoje = Calendar.getInstance();
        hoje.setTime(new Date());
        Map<String, Integer> diasRestantesMap = new HashMap<>();

        long diasRestantesParaMenstruacao = (getEndDate().toDate().getTime() - hoje.getTimeInMillis()) / (1000 * 60 * 60 * 24);
        diasRestantesMap.put("diasProximaMenstruacao", (int) diasRestantesParaMenstruacao);

        String faseAtual = "Sangramento";
        int diasRestantesParaFase = -1;

        for (Map.Entry<String, Map<String, Timestamp>> entry : fases.entrySet()) {
            String nomeFase = entry.getKey();
            Timestamp inicioFase = entry.getValue().get("inicio");
            Timestamp fimFase = entry.getValue().get("fim");

            if (hoje.getTimeInMillis() >= inicioFase.toDate().getTime() && hoje.getTimeInMillis() <= fimFase.toDate().getTime()) {
                faseAtual = nomeFase;
                break;
            } else if (hoje.getTimeInMillis() < inicioFase.toDate().getTime()) {
                diasRestantesParaFase = (int) ((inicioFase.toDate().getTime() - hoje.getTimeInMillis()) / (1000 * 60 * 60 * 24));
                break;
            }
        }
        diasRestantesMap.put("diasProximaFase", diasRestantesParaFase);
        return diasRestantesMap;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    public int calcularDuracao() {
        if (startDate != null && endDate != null) {
            long diffInMillis = endDate.toDate().getTime() - startDate.toDate().getTime();
            return (int) (diffInMillis / (1000 * 60 * 60 * 24)) + 1;
        }
        return 0;
    }

    public Timestamp getStartDate() { return startDate; }
    public Timestamp getEndDate() { return endDate; }
    public int getDuration() { return duration; }
    public boolean isNatural() { return natural; }
    public int getBleeding() { return bleeding; }
    public String getUserID() { return userID; }
    public String getId() { return id; }
    public Map<String, Map<String, Timestamp>> getFases() {
        return fases;
    }
}
