package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import br.edu.fatecsaocaetano.hystera.R;

public class NotificationHelper {

    // Canais de notificação
    public static final String CHANNEL_ID_CYCLE = "cycle_notifications";
    public static final String CHANNEL_ID_MEDICATION = "medication_notifications";
    private static final String CHANNEL_NAME_CYCLE = "Cycle Notifications";
    private static final String CHANNEL_NAME_MEDICATION = "Medication Notifications";
    private final Context context;
    private final NotificationManager notificationManager;
    private final FirebaseFirestore db;
    private boolean notificationsEnabled;
    private static final String tag = "NotificationHelperClass";

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.db = FirebaseFirestore.getInstance();
        this.notificationsEnabled = true; // Habilitado por padrão
        createNotificationChannels();
    }

    /**
     * Criação dos canais de notificação.
     */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel cycleChannel = new NotificationChannel(
                    CHANNEL_ID_CYCLE,
                    CHANNEL_NAME_CYCLE,
                    NotificationManager.IMPORTANCE_HIGH
            );
            cycleChannel.setDescription("Notificações sobre o ciclo menstrual.");
            notificationManager.createNotificationChannel(cycleChannel);

            NotificationChannel medicationChannel = new NotificationChannel(
                    CHANNEL_ID_MEDICATION,
                    CHANNEL_NAME_MEDICATION,
                    NotificationManager.IMPORTANCE_HIGH
            );
            medicationChannel.setDescription("Notificações para medicamentos.");
            notificationManager.createNotificationChannel(medicationChannel);

            Log.d(tag, "Canais de notificação criados.");
        }
    }

    /**
     * Enviar notificação imediata.
     */
    public void sendImmediateNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_CYCLE)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    /**
     * Habilitar notificações.
     */
    public void enableNotifications() {
        this.notificationsEnabled = true;
        Log.d(tag, "Notificações habilitadas.");
    }

    /**
     * Desabilitar notificações.
     */
    public void disableNotifications() {
        this.notificationsEnabled = false;
        cancelAllNotifications();
        Log.d(tag, "Notificações desabilitadas.");
    }

    /**
     * Cancelar todas as notificações.
     */
    private void cancelAllNotifications() {
        notificationManager.cancelAll();
        Log.d(tag, "Todas as notificações foram canceladas.");
    }

    /**
     * Agendar notificações para medicamentos.
     */
    public void scheduleMedicationNotifications(String userID) {
        if (!notificationsEnabled) {
            Log.d(tag, "Notificações estão desabilitadas.");
            return;
        }

        // Recupera todos os medicamentos do banco de dados para o usuário
        db.collection("Usuarios")
                .document(userID)
                .collection("Medicines")
                .get()  // Usando .get() para recuperar todos os medicamentos
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d(tag, "Nenhum medicamento encontrado.");
                        return;
                    }

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        // Processa cada medicamento individualmente
                        processMedicationDocument(document.getData());
                    }
                })
                .addOnFailureListener(e -> Log.e(tag, "Erro ao recuperar medicamentos.", e));
    }


    /**
     * Processar documento de medicamentos para agendamento de notificações.
     */
    private void processMedicationDocument(Map<String, Object> data) {
        Boolean notificationEnabled = (Boolean) data.get("notification");
        Boolean sound = (Boolean) data.get("sound");
        Boolean vibration = (Boolean) data.get("vibration");
        Number trigger = (Number) data.get("trigger");
        Timestamp drugDate = (Timestamp) data.get("drugDate");
        Log.e(tag, "drugDate (calculado) " + drugDate);
        String drugName = (String) data.get("drugName");

        if (notificationEnabled != null && notificationEnabled && drugDate != null && trigger != null) {
            long interval = trigger.longValue() * 3600 * 1000;
            scheduleMedicationNotification(drugName.hashCode(), drugName, drugDate, interval, sound, vibration);
        }
    }

    /**
     * Agendar notificação para medicamento.
     */
    private void scheduleMedicationNotification(int id, String drugName, Timestamp startTime, long interval, Boolean sound, Boolean vibration) {
        long startMillis = startTime.getSeconds() * 1000L; // Hora em UTC (milissegundos)
        long currentMillis = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startMillis); // O fuso horário já será ajustado para o local

        // Agora, `calendar.getTimeInMillis()` vai retornar o horário ajustado para o fuso horário de Brasília
        long adjustedStartMillis = calendar.getTimeInMillis();

        // Somar o intervalo (em milissegundos) ao horário ajustado para o fuso horário
        long triggerTime = adjustedStartMillis + interval;

        // Log para depuração
        Log.e(tag, "triggerTime (calculado) " + triggerTime);
        Log.e(tag, "startMillis " + startMillis);
        Log.e(tag, "adjustedStartMillis " + adjustedStartMillis);
        Log.e(tag, "interval " + interval);

        // Verifica se o triggerTime está no passado e ajusta
        while (triggerTime < currentMillis) {
            triggerTime += interval;  // Ajuste o triggerTime caso ele já tenha passado
        }

        // Criar a Intent para a notificação
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("message", "Hora de tomar o medicamento: " + drugName);
        intent.putExtra("sound", sound);
        intent.putExtra("vibration", vibration);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            try {
                // Agendar o alarme com o horário calculado
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                } else {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, interval, pendingIntent);
                }
                Log.d(tag, "Notificação agendada para " + drugName + " às " + triggerTime);
            } catch (SecurityException e) {
                Log.e(tag, "Erro ao agendar alarme exato: ", e);
            }
        }
    }


    /**
     * Agendar notificações de fases do ciclo.
     */
    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleCyclePhaseNotifications(Context context, String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Usuarios").document(userId).collection("Ciclos")
                .orderBy("startDate", Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot latestCycle = queryDocumentSnapshots.getDocuments().get(0);
                        Map<String, Map<String, Timestamp>> fases = (Map<String, Map<String, Timestamp>>) latestCycle.get("fases");

                        if (fases != null) {
                            for (Map.Entry<String, Map<String, Timestamp>> entry : fases.entrySet()) {
                                String fase = entry.getKey();
                                Map<String, Timestamp> timestamps = entry.getValue();
                                Timestamp inicio = timestamps.get("inicio");

                                if (inicio != null) {
                                    long triggerTime = inicio.getSeconds() * 1000;

                                    if (System.currentTimeMillis() < triggerTime) {
                                        PendingIntent pendingIntent = createPendingIntent(context, userId, fase);
                                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                                        if (alarmManager != null) {
                                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                                        }
                                    }
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(tag, "Erro ao agendar notificações para fases do ciclo: ", e));
    }

    /**
     * Criar PendingIntent para notificações de fases.
     */
    private static PendingIntent createPendingIntent(Context context, String userId, String phase) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction("PHASE_NOTIFICATION");
        intent.putExtra("userId", userId);
        intent.putExtra("phase", phase);

        return PendingIntent.getBroadcast(
                context,
                phase.hashCode(), // ID único baseado na fase
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

}
