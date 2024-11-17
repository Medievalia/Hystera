package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.graphics.Color;
import android.util.Log;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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

    public void adicionarFase(String nomeFase, Timestamp inicio, Timestamp fim) {
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

        // Zera a hora, minuto, segundo e milissegundo da data de hoje
        hoje.set(Calendar.HOUR_OF_DAY, 0);
        hoje.set(Calendar.MINUTE, 0);
        hoje.set(Calendar.SECOND, 0);
        hoje.set(Calendar.MILLISECOND, 0);

        Map<String, String> informacoesCiclo = new HashMap<>();

        long diasDesdeInicio = (hoje.getTimeInMillis() - getStartDate().toDate().getTime()) / (1000 * 60 * 60 * 24);
        int diaDoCiclo = (int) diasDesdeInicio + 1;

        String faseAtual = "";
        for (Map.Entry<String, Map<String, Timestamp>> entry : fases.entrySet()) {
            String nomeFase = entry.getKey();
            Timestamp inicioFase = entry.getValue().get("inicio");
            Timestamp fimFase = entry.getValue().get("fim");

            // Cria uma instância Calendar para as datas de início e fim da fase
            Calendar inicioFaseCalendar = Calendar.getInstance();
            Calendar fimFaseCalendar = Calendar.getInstance();

            // Configura as datas de início e fim da fase, zerando hora, minuto, segundo e milissegundo
            inicioFaseCalendar.setTime(inicioFase.toDate());
            fimFaseCalendar.setTime(fimFase.toDate());

            // Zera a hora, minuto, segundo e milissegundo das fases
            inicioFaseCalendar.set(Calendar.HOUR_OF_DAY, 0);
            inicioFaseCalendar.set(Calendar.MINUTE, 0);
            inicioFaseCalendar.set(Calendar.SECOND, 0);
            inicioFaseCalendar.set(Calendar.MILLISECOND, 0);

            fimFaseCalendar.set(Calendar.HOUR_OF_DAY, 0);
            fimFaseCalendar.set(Calendar.MINUTE, 0);
            fimFaseCalendar.set(Calendar.SECOND, 0);
            fimFaseCalendar.set(Calendar.MILLISECOND, 0);

            // Verifica se a data de hoje está entre o início e o fim da fase (inclusive)
            if ((hoje.equals(inicioFaseCalendar) || hoje.after(inicioFaseCalendar)) &&
                    (hoje.equals(fimFaseCalendar) || hoje.before(fimFaseCalendar))) {
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

        int diasRestantesParaFase = -1;

        for (Map.Entry<String, Map<String, Timestamp>> entry : fases.entrySet()) {
            Timestamp inicioFase = entry.getValue().get("inicio");
            if (hoje.getTimeInMillis() < inicioFase.toDate().getTime()) {
                diasRestantesParaFase = (int) ((inicioFase.toDate().getTime() - hoje.getTimeInMillis()) / (1000 * 60 * 60 * 24));
                break;
            }
        }
        diasRestantesMap.put("diasProximaFase", diasRestantesParaFase);
        return diasRestantesMap;
    }

    public int calcularDuracao() {
        if (startDate != null && endDate != null) {
            long diffInMillis = endDate.toDate().getTime() - startDate.toDate().getTime();
            return (int) (diffInMillis / (1000 * 60 * 60 * 24)) + 1;
        }
        return 0;
    }

    public void marcarDiasNoCalendario(MaterialCalendarView calendarView, Calendar start, Calendar end, Cycle ciclo) {
        if (ciclo.getStartDate() == null || ciclo.getOvulation() == null) {
            Log.e(tag, "startDate ou a data da ovulação são nulas: " + ciclo.getStartDate() + "," + ciclo.getOvulation());
            return;
        }

        Set<CalendarDay> bleedingDays = new HashSet<>();
        Set<CalendarDay> fertileDays = new HashSet<>();

        // Marcando dias de sangramento
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ciclo.getStartDate().toDate());

        // Marcar dias de sangramento
        for (int i = 0; i < ciclo.getBleeding(); i++) {
            Calendar day = (Calendar) calendar.clone();
            day.add(Calendar.DAY_OF_MONTH, i);
            if ((day.compareTo(start) >= 0 && day.compareTo(end) <= 0) || (day.get(Calendar.MONTH) != start.get(Calendar.MONTH))) {
                bleedingDays.add(CalendarDay.from(day));
            }
        }

        // Marcando dias férteis
        Calendar ovulacao = Calendar.getInstance();
        ovulacao.setTime(ciclo.getOvulation().toDate());

        for (int i = -2; i <= 2; i++) {
            Calendar fertileDay = (Calendar) ovulacao.clone();
            fertileDay.add(Calendar.DAY_OF_MONTH, i);
            if (fertileDay.compareTo(start) >= 0 && fertileDay.compareTo(end) <= 0) {
                fertileDays.add(CalendarDay.from(fertileDay));
            }
        }

        int bleedingColor = Color.parseColor("#D84242");
        int fertileColor = Color.parseColor("#61C3EF");
        calendarView.addDecorator(new EventDecorator(bleedingColor, bleedingDays));
        calendarView.addDecorator(new EventDecorator(fertileColor, fertileDays));
    }

    public boolean isInterrupted() {
        return interrupted;
    }

    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    // Getters and setters
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Map<String, Timestamp>> getFases() {
        return fases;
    }
}
