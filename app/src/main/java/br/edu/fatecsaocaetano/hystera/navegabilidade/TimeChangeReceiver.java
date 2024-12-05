package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TimeChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_TIME_CHANGED.equals(intent.getAction()) ||
                Intent.ACTION_DATE_CHANGED.equals(intent.getAction())) {
            // Reagendar notificações quando a data ou o horário mudarem
            NotificationHelper notificationHelper = new NotificationHelper(context);

            // Reagendar notificações para medicamentos e fases do ciclo
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                notificationHelper.scheduleCyclePhaseNotifications(context, userId);
                notificationHelper.scheduleMedicationNotifications(userId);
            }
        }
    }
}
