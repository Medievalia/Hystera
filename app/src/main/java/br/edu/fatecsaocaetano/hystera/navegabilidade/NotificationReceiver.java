package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import br.edu.fatecsaocaetano.hystera.R;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");

        // Criação da notificação
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID_CYCLE)
                .setSmallIcon(R.drawable.diu)  // Certifique-se de que o ícone está correto
                .setContentTitle("Notificação")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true); // A notificação será cancelada quando o usuário clicar nela

        // Gerenciar a notificação para dispositivos com Android 8.0 (Oreo) ou superior
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = NotificationHelper.CHANNEL_ID_CYCLE; // Ou outro canal conforme necessário
            Notification notification = builder.build();
            notificationManager.notify((int) System.currentTimeMillis(), notification);  // ID único com base no tempo
        } else {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());  // ID único com base no tempo
        }

        // Log para verificar se a notificação foi recebida
        Log.d("NotificationReceiver", "Received notification with message: " + message);
        Log.d("NotificationReceiver", "Notification sent");
    }
}
