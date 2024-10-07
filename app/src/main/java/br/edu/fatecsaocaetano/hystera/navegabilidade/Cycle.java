package br.edu.fatecsaocaetano.hystera.navegabilidade;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Calendar;
public class Cycle {
    private static final Logger logger = LoggerUtils.configurarLogger(Cycle.class.getName());
    private String userID;
    private String id;
    private Timestamp startDate;
    private Timestamp endDate;
    private int duration;
    private boolean natural;
    private int bleeding;
    private Timestamp ovulation;
    private int average;
    private Map<String, Timestamp> faseLutea = new HashMap<>();
    private Map<String, Timestamp> faseFolicular = new HashMap<>();
    private Map<String, Timestamp> faseMenstrual = new HashMap<>();
    private Map<String, Timestamp> faseOvulacao = new HashMap<>();

    private Map<String, Map<String, Timestamp>> fases = new HashMap<>();


    public Cycle(Timestamp startDate, int duration, boolean natural, int bleeding) {
        this.startDate = startDate;
        this.duration = duration;
        this.natural = natural;
        this.bleeding = bleeding;

        this.endDate = adicionarDias(startDate, duration);
        this.ovulation = calcularOvulacao();

        this.id = gerarIdCiclo();

        // Adiciona as fases
        adicionarFase(faseMenstrual, startDate, adicionarDias(startDate, bleeding));
        adicionarFase(faseFolicular, adicionarDias(startDate, bleeding), ovulation);
        adicionarFase(faseOvulacao, ovulation, ovulation); // A ovulação ocorre em um único dia
        adicionarFase(faseLutea, adicionarDias(ovulation, 1), endDate);
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isNatural() {
        return natural;
    }

    public void setNatural(boolean natural) {
        this.natural = natural;
    }

    public int getBleeding() {
        return bleeding;
    }

    public void setBleeding(int bleeding) {
        this.bleeding = bleeding;
    }

    public Timestamp getOvulation() {
        return ovulation;
    }

    public void setOvulation(Timestamp ovulation) {
        this.ovulation = ovulation;
    }

    public int getAverage() {
        return average;
    }

    public void setAverage(int average) {
        this.average = average;
    }

    public Map<String, Timestamp> getFaseLutea() {
        return faseLutea;
    }

    public void setFaseLutea(Map<String, Timestamp> faseLutea) {
        this.faseLutea = faseLutea;
    }

    public Map<String, Timestamp> getFaseFolicular() {
        return faseFolicular;
    }

    public void setFaseFolicular(Map<String, Timestamp> faseFolicular) {
        this.faseFolicular = faseFolicular;
    }

    public Map<String, Timestamp> getFaseMenstrual() {
        return faseMenstrual;
    }

    public void setFaseMenstrual(Map<String, Timestamp> faseMenstrual) {
        this.faseMenstrual = faseMenstrual;
    }

    public Map<String, Timestamp> getFaseOvulacao() {
        return faseOvulacao;
    }

    public void setFaseOvulacao(Map<String, Timestamp> faseOvulacao) {
        this.faseOvulacao = faseOvulacao;
    }

    public void adicionarFase(Map<String, Timestamp> fase, Timestamp inicio, Timestamp fim) {
        fase.put("inicio", inicio);
        fase.put("fim", fim);
    }

    public String gerarIdCiclo() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMyyyy", Locale.getDefault());
        return sdf.format(startDate.toDate());
    }

    public void definirId() {
        this.id = gerarIdCiclo();
    }

    public String getId() {
        return id;
    }

    public void saveCycle(Cycle cycle) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        try {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "ID do usuário não encontrado!");
        }

        // Criar um ID único baseado em mês e ano
        SimpleDateFormat sdf = new SimpleDateFormat("MMyyyy", Locale.getDefault());
        String mesano = sdf.format(cycle.getStartDate().toDate());

        // Referência para a coleção de ciclos
        DocumentReference cycleRef = db.collection("Usuarios").document(userID)
                .collection("Ciclos").document(mesano);

        // Salvar o ciclo
        cycleRef.set(cycle)
                .addOnSuccessListener(aVoid -> logger.log(Level.INFO, "Ciclo salvo com sucesso! " + userID))
                .addOnFailureListener(e -> logger.log(Level.SEVERE, "Erro ao salvar ciclo. " + userID + " " + e));
    }

    public Timestamp adicionarDias(Timestamp timestamp, int diasParaAdicionar) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp.toDate());

        // Adicionar os dias ao calendário
        calendar.add(Calendar.DAY_OF_MONTH, diasParaAdicionar);

        // Converter de volta para Timestamp e retornar
        return new Timestamp(calendar.getTime());
    }

    public Timestamp removerDias(Timestamp timestamp, int diasParaRemover) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp.toDate());

        // Remover os dias do calendário
        calendar.add(Calendar.DAY_OF_MONTH, -diasParaRemover);

        // Converter de volta para Timestamp e retornar
        return new Timestamp(calendar.getTime());
    }

    private Timestamp calcularOvulacao() {
        return removerDias(endDate, 14);
    }

    public void salvarCicloNoFirebase() {
        saveCycle(this); // Chama o método interno passando o ciclo atual
    }

    public void adicionarFase(String nomeFase, Timestamp inicio, Timestamp fim) {
        Map<String, Timestamp> fase = new HashMap<>();
        fase.put("inicio", inicio);
        fase.put("fim", fim);
        fases.put(nomeFase, fase);
    }
}
