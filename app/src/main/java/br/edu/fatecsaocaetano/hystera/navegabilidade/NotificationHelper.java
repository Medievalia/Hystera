package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.Timestamp;

public class NotificationHelper {

    public static final String CHANNEL_ID_CYCLE = "cycle_notifications";
    private static final String CHANNEL_ID_MEDICATION = "medication_notifications";
    private static final String CHANNEL_NAME_CYCLE = "Cycle Notifications";
    private static final String CHANNEL_NAME_MEDICATION = "Medication Notifications";
    private Context context;
    private NotificationManager notificationManager;
    private FirebaseFirestore db;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.db = FirebaseFirestore.getInstance();

        createNotificationChannels();
    }

    // Criar os canais de notificação para ciclos e medicamentos
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

            Log.d("NotificationHelper", "Notification channels created successfully.");
        }
    }

    // Verificar se o aplicativo tem permissão para agendar alarmes exatos
    private boolean hasExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            return alarmManager != null && alarmManager.canScheduleExactAlarms();
        }
        return true; // Para versões abaixo do Android 12, não é necessário pedir permissão
    }

    // Solicitar permissão de alarmes exatos
    private void requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !hasExactAlarmPermission()) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            context.startActivity(intent);
        }
    }

    // Agendar a notificação de um medicamento
    public void scheduleMedicationNotifications(String userID) {
        // Verifica e solicita permissão antes de agendar notificações
        requestExactAlarmPermission();

        db.collection("Usuarios")
                .document(userID)
                .collection("Medicines")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String drugName = document.getString("drugName");
                            Timestamp drugDate = document.getTimestamp("drugDate");
                            boolean notificationEnabled = document.getBoolean("notification");

                            if (notificationEnabled && drugDate != null) {
                                scheduleMedicationNotification(drugName, drugDate);
                            }
                        }
                    } else {
                        Log.e("NotificationHelper", "Error getting medications", task.getException());
                    }
                });
    }

    // Agendar a notificação de um medicamento
    private void scheduleMedicationNotification(String drugName, Timestamp drugDate) {
        long triggerTime = drugDate.getSeconds() * 1000L;  // Convertendo timestamp para milissegundos

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("message", "Hora de tomar o medicamento: " + drugName);

        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(context, drugName.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context, drugName.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    // Testar a notificação de ciclo menstrual
    public void scheduleCycleNotifications(String userID) {
        // Verifica e solicita permissão antes de agendar notificações
        requestExactAlarmPermission();

        db.collection("Usuarios")
                .document(userID)
                .collection("Ciclos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Timestamp cycleStart = document.getTimestamp("startDate");

                            if (cycleStart != null) {
                                scheduleCycleNotification(cycleStart);
                            }
                        }
                    } else {
                        Log.e("NotificationHelper", "Error getting cycle data", task.getException());
                    }
                });
    }

    // Agendar a notificação para o início do ciclo menstrual
    private void scheduleCycleNotification(Timestamp cycleStart) {
        long triggerTime = cycleStart.getSeconds() * 1000L;  // Convertendo timestamp para milissegundos

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("message", "Seu ciclo menstrual vai começar!");

        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }
}
