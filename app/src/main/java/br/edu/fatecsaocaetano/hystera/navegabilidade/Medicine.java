package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import br.edu.fatecsaocaetano.hystera.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Medicine extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicacao);

        //navegação e menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationHelper menuHelper = new BottomNavigationHelper();
        menuHelper.setNavigationFocus(bottomNavigationView, R.id.nav_medicacao);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_anota) {
                    startActivity(new Intent(Medicine.this, Notes.class));
                    startActivity(new Intent(Medicine.this, Annotations.class));
                    return true;
                } else if (id == R.id.nav_calendario) {
                    startActivity(new Intent(Medicine.this, CalendaryCycle.class));
                    return true;
                } else if (id == R.id.nav_utero) {
                    startActivity(new Intent(Medicine.this, Informations.class));
                    return true;
                } else if (id == R.id.nav_seekbar) {
                    startActivity(new Intent(Medicine.this, TimeLine.class));
                    return true;
                } else if (id == R.id.nav_medicacao) {
                    startActivity(new Intent(Medicine.this, Medicine.class));
                    return true;
                }
                return false;
            }
        });
    }
}