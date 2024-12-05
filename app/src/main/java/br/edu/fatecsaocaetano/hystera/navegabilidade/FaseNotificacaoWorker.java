package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import javax.xml.transform.Result;

import br.edu.fatecsaocaetano.hystera.R;

public class FaseNotificacaoWorker extends Worker {

    private static final String CHANNEL_ID = "cycle_channel";

    public FaseNotificacaoWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String fase = getTags().iterator().next(); // Recupera a fase associada

        // Criar canal de notificação (somente para Android 8+)
        criarCanalDeNotificacao();

        // Criar e exibir notificação
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.logo) // Ícone da notificação
                .setContentTitle("Hystera: Nova Fase")
                .setContentText("Você entrou na fase: " + fase)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(fase.hashCode(), builder.build());

        return Result.success();
    }

    private void criarCanalDeNotificacao() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notificações do Ciclo",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notificações relacionadas às fases do ciclo");
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
