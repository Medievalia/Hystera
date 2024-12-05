package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

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
        this.notificationsEnabled = true;
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

        db.collection("Usuarios")
                .document(userID)
                .collection("Medicines")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(tag, "Erro ao ouvir alterações nos medicamentos.", e);
                        return;
                    }
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            processMedicationDocument(dc.getDocument().getData());
                        }
                    }
                });
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
        String drugName = (String) data.get("drugName");

        if (notificationEnabled != null && notificationEnabled && drugDate != null && trigger != null) {
            long interval = trigger.longValue() * 3600000L;
            scheduleMedicationNotification(drugName.hashCode(), drugName, drugDate, interval, sound, vibration);
        }
    }

    public static long calculateTriggerTime(long drugTimestamp, long intervalMillis) {
        long currentTime = System.currentTimeMillis(); // Em UTC

        // Calcular o triggerTime inicial
        long triggerTime = drugTimestamp + intervalMillis;

        // Ajustar o triggerTime se já tiver passado
        while (triggerTime < currentTime) {
            triggerTime += intervalMillis;
        }

        // Registrar os horários legíveis para depuração (sem conversão de fuso horário)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Log.d("DEBUG", "Start Time (Date): " + sdf.format(new Date(drugTimestamp)));
        Log.d("DEBUG", "IntervalMillis: " + intervalMillis);
        Log.d("DEBUG", "Trigger Time (Date): " + sdf.format(new Date(triggerTime)));
        Log.d("DEBUG", "Current Time (Date): " + sdf.format(new Date(currentTime)));

        return triggerTime;
    }

    private void scheduleMedicationNotification(int id, String drugName, Timestamp startTime, long interval, Boolean sound, Boolean vibration) {
        long startMillis = startTime.getSeconds() * 1000;
        long currentMillis = System.currentTimeMillis();

        long triggerTime = calculateTriggerTime(startMillis, interval);

        Log.d("DEBUG", "Start Time (UTC): " + startMillis);
        Log.d("DEBUG", "Trigger Time: " + triggerTime + ", Current Time: " + currentMillis);

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("type", "medication");
        intent.putExtra("message", "Tomar o medicamento " + drugName);
        intent.putExtra("sound", true);
        intent.putExtra("vibration", true);
        context.sendBroadcast(intent);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                    Log.d(tag, "Alarme exato agendado para: " + new Date(triggerTime));
                } else {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, interval, pendingIntent);
                    Log.d(tag, "Alarme repetitivo agendado para: " + new Date(triggerTime));
                }
            } catch (SecurityException e) {
                Log.e(tag, "Erro ao agendar alarme exato: ", e);
            }
        } else {
            Log.e(tag, "AlarmManager está nulo. Não foi possível agendar.");
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
                            long currentTime = System.currentTimeMillis();
                            String currentPhase = null;

                            for (Map.Entry<String, Map<String, Timestamp>> entry : fases.entrySet()) {
                                String fase = entry.getKey();
                                Map<String, Timestamp> timestamps = entry.getValue();
                                Timestamp inicio = timestamps.get("inicio");
                                Timestamp fim = timestamps.get("fim");

                                if (inicio != null && fim != null) {
                                    long startTime = inicio.getSeconds() * 1000;
                                    long endTime = fim.getSeconds() * 1000;

                                    // Verificar se o horário atual está dentro da fase
                                    if (currentTime >= startTime && currentTime <= endTime) {
                                        currentPhase = fase;
                                        break;
                                    }
                                }
                            }

                            // Notificar a fase atual, se encontrada
                            if (currentPhase != null) {
                                NotificationHelper helper = new NotificationHelper(context);
                                helper.sendImmediateNotification(
                                        "Fase Atual do Ciclo",
                                        "Você está na fase: " + currentPhase
                                );
                            }

                            // Continuar agendando notificações futuras
                            for (Map.Entry<String, Map<String, Timestamp>> entry : fases.entrySet()) {
                                String fase = entry.getKey();
                                Map<String, Timestamp> timestamps = entry.getValue();
                                Timestamp inicio = timestamps.get("inicio");

                                if (inicio != null) {
                                    long triggerTime = inicio.getSeconds() * 1000;

                                    if (currentTime < triggerTime) {
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
                phase.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

}
