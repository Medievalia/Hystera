package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import br.edu.fatecsaocaetano.hystera.R;

import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Notifications extends AppCompatActivity {

    private NotificationHelper notificationHelper;
    private final String tag = "NotificationsClass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacoes);

        notificationHelper = new NotificationHelper(this);
        //setupFirebaseListeners();
        sendTestNotification();

        //navegação e menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationHelper menuHelper = new BottomNavigationHelper();
        menuHelper.setNavigationFocus(bottomNavigationView, R.id.nav_seekbar);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_anota) {
                    startActivity(new Intent(Notifications.this, Notes.class));
                    startActivity(new Intent(Notifications.this, Annotations.class));
                    return true;
                } else if (id == R.id.nav_calendario) {
                    startActivity(new Intent(Notifications.this, CalendarCycle.class));
                    return true;
                } else if (id == R.id.nav_utero) {
                    startActivity(new Intent(Notifications.this, Informations.class));
                    return true;
                } else if (id == R.id.nav_seekbar) {
                    startActivity(new Intent(Notifications.this, TimeLine.class));
                    return true;
                } else if (id == R.id.nav_medicacao) {
                    startActivity(new Intent(Notifications.this, Medicine.class));
                    return true;
                }
                return false;
            }
        });

        SwitchMaterial notificationSwitch = findViewById(R.id.notificationSwitch);
        SwitchMaterial soundSwitch = findViewById(R.id.soundSwitch);
        SwitchMaterial vibrationSwitch = findViewById(R.id.vibrationSwitch);

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sendNotification("title", "message");
            } else {
                Toast.makeText(this, "Notificações desativadas", Toast.LENGTH_SHORT).show();
            }
        });

        // Listener para o switch de som
        soundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, isChecked ? "Som ativado" : "Som desativado", Toast.LENGTH_SHORT).show();
        });

        // Listener para o switch de vibração
        vibrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, isChecked ? "Vibração ativada" : "Vibração desativada", Toast.LENGTH_SHORT).show();
        });
    }

    private void sendNotification(String t, String m) {
        // Configurações de título e mensagem
        String title = t;
        String message = m;
        int id = generateID();

        // Enviar notificação usando o NotificationHelper
        //notificationHelper.sendCycleNotification(title, message, id);
    }

    private int generateID(){
        return 1;
    }

    private void sendTestNotification() {
        // Verifica se o app tem permissão para agendar alarmes exatos
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                // Se não tiver permissão, solicitar permissão ao usuário
                requestExactAlarmPermission();
                return;  // Não agendar o alarme até que a permissão seja concedida
            }
        }

        // Configurar uma notificação de teste que será disparada em 5 segundos
        long triggerTime = System.currentTimeMillis() + 5000; // 5 segundos a partir de agora

        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("message", "Esta é uma notificação de teste!");
        Log.e("que", "erro");

        // Usar FLAG_IMMUTABLE ou FLAG_MUTABLE
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    private void requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivity(intent);
        }
    }

}


