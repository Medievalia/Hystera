package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import br.edu.fatecsaocaetano.hystera.R;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Notifications extends AppCompatActivity {

    private NotificationHelper notificationHelper;
    private final String tag = "NotificationsClass";
    private MaterialButton voltarButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacoes);
        voltarButton = findViewById(R.id.voltar_button);

        notificationHelper = new NotificationHelper(this);
        // Configuração dos switches
        SwitchMaterial notificationSwitch = findViewById(R.id.notificationSwitch);
        SwitchMaterial soundSwitch = findViewById(R.id.soundSwitch);
        SwitchMaterial vibrationSwitch = findViewById(R.id.vibrationSwitch);

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

        // Configuração inicial dos switches com SharedPreferences
        sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);

        // Recuperando os valores de preferências para o estado inicial
        boolean areNotificationsEnabled = sharedPreferences.getBoolean("notificationsEnabled", true);
        boolean isSoundEnabled = sharedPreferences.getBoolean("soundEnabled", true);
        boolean isVibrationEnabled = sharedPreferences.getBoolean("vibrationEnabled", true);

        // Configura os switches de acordo com as preferências
        notificationSwitch.setChecked(areNotificationsEnabled);
        soundSwitch.setChecked(isSoundEnabled);
        vibrationSwitch.setChecked(isVibrationEnabled);

        // Ativar/desativar notificações
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "Notificações ativadas", Toast.LENGTH_SHORT).show();
                notificationHelper.enableNotifications();
            } else {
                Toast.makeText(this, "Notificações desativadas", Toast.LENGTH_SHORT).show();
                notificationHelper.disableNotifications();
            }

            // Salvar a preferência
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notificationsEnabled", isChecked);
            editor.apply();
        });

        // Som
        soundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, isChecked ? "Som ativado" : "Som desativado", Toast.LENGTH_SHORT).show();
            // Salvar a preferência
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("soundEnabled", isChecked);
            editor.apply();
        });

        // Vibração
        vibrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, isChecked ? "Vibração ativada" : "Vibração desativada", Toast.LENGTH_SHORT).show();
            // Salvar a preferência
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("vibrationEnabled", isChecked);
            editor.apply();
        });

        // Botão voltar
        voltarButton.setOnClickListener(v -> finish());
    }

    // Solicitar permissão para alarmes exatos, caso necessário
    private void requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivity(intent);
        }
    }
}