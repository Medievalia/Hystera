package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import br.edu.fatecsaocaetano.hystera.R;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String tag = "NotificationReceiverClass";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            Log.e(tag, "NotificationManager não está disponível.");
            return;
        }

        if (action == null) {
            Log.e(tag, "Ação da intent é nula.");
            return;
        }

        switch (action) {
            case "PHASE_NOTIFICATION":
                handlePhaseNotification(context, notificationManager, intent);
                break;

            case "MEDICATION_NOTIFICATION":
                handleMedicationNotification(context, notificationManager, intent);
                break;

            default:
                Log.w(tag, "Ação desconhecida: " + action);
        }
    }

    /**
     * Processar notificações de mudanças de fase do ciclo.
     */
    private void handlePhaseNotification(Context context, NotificationManager notificationManager, Intent intent) {
        String phase = intent.getStringExtra("phase");
        if (phase == null) {
            Log.e(tag, "Fase não encontrada na intent.");
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID_CYCLE)
                .setSmallIcon(R.drawable.logo) // Atualize com o ícone correto
                .setContentTitle("Mudança de Fase")
                .setContentText("Você entrou na fase: " + phase)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(phase.hashCode(), builder.build());
        Log.d(tag, "Notificação de fase enviada: " + phase);
    }

    /**
     * Processar notificações de medicamentos.
     */
    private void handleMedicationNotification(Context context, NotificationManager notificationManager, Intent intent) {
        String message = intent.getStringExtra("message");
        boolean sound = intent.getBooleanExtra("sound", false);
        boolean vibration = intent.getBooleanExtra("vibration", false);

        if (message == null) {
            Log.e(tag, "Mensagem não encontrada na intent.");
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID_MEDICATION)
                .setSmallIcon(R.drawable.logo) // Atualize com o ícone correto
                .setContentTitle("Lembrete de Medicamento")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (sound) {
            builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        }

        if (vibration) {
            builder.setVibrate(new long[]{0, 500, 1000}); // Vibração customizada
        } else {
            builder.setVibrate(null);
        }

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        Log.d(tag, "Notificação de medicamento enviada: " + message);
    }
}
