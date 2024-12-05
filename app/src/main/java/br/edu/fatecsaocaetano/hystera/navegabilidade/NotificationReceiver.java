package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import br.edu.fatecsaocaetano.hystera.R;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReceiverClass";
    private static final String CHANNEL_ID_MEDICATION = "medication_notifications";
    private static final String CHANNEL_ID_CYCLE = "cycle_notifications";

    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra("type");

        if (type == null) {
            Log.e(TAG, "Tipo de notificação não encontrado na intent.");
            return;
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            Log.e(TAG, "NotificationManager não encontrado.");
            return;
        }

        // Criar canais de notificação (necessário para Android 8+)
        createNotificationChannels(context, notificationManager);

        switch (type) {
            case "medication":
                handleMedicationNotification(context, notificationManager, intent);
                break;
            case "cycle_phase":
                handlePhaseNotification(context, notificationManager, intent);
                break;
            default:
                Log.e(TAG, "Tipo de notificação inválido: " + type);
        }
    }

    /**
     * Criação dos canais de notificação.
     */
    private void createNotificationChannels(Context context, NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel medicationChannel = new NotificationChannel(
                    CHANNEL_ID_MEDICATION,
                    "Notificações de Medicamentos",
                    NotificationManager.IMPORTANCE_HIGH
            );
            medicationChannel.setDescription("Notificações para lembrar de tomar medicamentos.");

            NotificationChannel cycleChannel = new NotificationChannel(
                    CHANNEL_ID_CYCLE,
                    "Notificações de Fases do Ciclo",
                    NotificationManager.IMPORTANCE_HIGH
            );
            cycleChannel.setDescription("Notificações sobre mudanças nas fases do ciclo menstrual.");

            notificationManager.createNotificationChannel(medicationChannel);
            notificationManager.createNotificationChannel(cycleChannel);
        }
    }

    /**
     * Processar notificações de medicamentos.
     */
    private void handleMedicationNotification(Context context, NotificationManager notificationManager, Intent intent) {
        String message = intent.getStringExtra("message");
        boolean sound = intent.getBooleanExtra("sound", true);
        boolean vibration = intent.getBooleanExtra("vibration", true);

        if (message == null) {
            Log.e(TAG, "Mensagem de notificação de medicamento não encontrada.");
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_MEDICATION)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Lembrete de Medicamento")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (sound) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(alarmSound);
        }

        if (vibration) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(500);
                }
            }
        }

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        Log.d(TAG, "Notificação de medicamento enviada: " + message);
    }

    /**
     * Processar notificações de mudanças de fase do ciclo.
     */
    private void handlePhaseNotification(Context context, NotificationManager notificationManager, Intent intent) {
        String phase = intent.getStringExtra("phase");

        if (phase == null) {
            Log.e(TAG, "Fase do ciclo não encontrada na intent.");
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_CYCLE)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Mudança de Fase")
                .setContentText("Você entrou na fase: " + phase)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(phase.hashCode(), builder.build());
        Log.d(TAG, "Notificação de fase enviada: " + phase);
    }
}
