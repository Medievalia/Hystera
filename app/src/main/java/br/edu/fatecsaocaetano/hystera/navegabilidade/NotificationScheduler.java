package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class NotificationScheduler {

    private static final String tag = "NotificationScheduler";

    public static void scheduleMedicationNotification(Context context, Drug drug) {
        if (drug.isNotification()) {  // Só notificar se "notification" for true
            long interval = calculateInterval(drug.getTrigger());

            // Cria a Intent para o BroadcastReceiver
            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.setAction("MEDICATION_NOTIFICATION");
            intent.putExtra("message", "Hora de tomar: " + drug.getDrugName() + " " + drug.getDrugAmount() + " " + drug.getDosageUnits());
            intent.putExtra("sound", drug.isSound());
            intent.putExtra("vibration", drug.isVibration());

            // PendingIntent para disparar a notificação
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    drug.getId().hashCode(), // Um ID único para o medicamento
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            // Obtém o AlarmManager para agendar a notificação
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if (alarmManager != null) {
                // Agenda a notificação com intervalo
                alarmManager.setRepeating(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + interval,
                        interval,
                        pendingIntent
                );
                Log.d(tag, "Notificação agendada para o medicamento: " + drug.getDrugName());
            } else {
                Log.e(tag, "AlarmManager não disponível.");
            }
        }
    }

    // Calcular o intervalo com base no trigger
    private static long calculateInterval(int trigger) {
        switch (trigger) {
            case 1:  // Notificar a cada 1 hora
                return AlarmManager.INTERVAL_HOUR;
            // Adicione mais casos conforme necessário
            default:
                return AlarmManager.INTERVAL_DAY; // Por padrão, notificar diariamente
        }
    }
}
