package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import br.edu.fatecsaocaetano.hystera.R;

public class Informations extends AppCompatActivity {

    String link1 = "https://cinge.com.br/2022/08/08/coletor-menstrual-ou-absorvente-qual-o-melhor/";
    String link2 = "https://mundoeducacao.uol.com.br/sexualidade/ciclo-menstrual.htm";
    String link3 = "https://www.pantys.com.br/blogs/pantys/absorvente-ecologico-vs-descartavel-5-diferencas";
    String link4 = "https://hilab.com.br/blog/pilula-dia-seguinte/";
    String link5 = "https://bvsms.saude.gov.br/endometriose/#:~:text=A%20endometriose%20%C3%A9%20uma%20doen%C3%A7a,geral%2C%20devem%20ser%20retiradas%20cirurgicamente.";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacao);

        //navegação e menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationHelper menuHelper = new BottomNavigationHelper();
        menuHelper.setNavigationFocus(bottomNavigationView, R.id.nav_utero);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_anota) {
                    startActivity(new Intent(Informations.this, Notes.class));
                    startActivity(new Intent(Informations.this, Annotations.class));
                    return true;
                } else if (id == R.id.nav_calendario) {
                    startActivity(new Intent(Informations.this, CalendaryCycle.class));
                    return true;
                } else if (id == R.id.nav_utero) {
                    startActivity(new Intent(Informations.this, Informations.class));
                    return true;
                } else if (id == R.id.nav_seekbar) {
                    startActivity(new Intent(Informations.this, TimeLine.class));
                    return true;
                } else if (id == R.id.nav_medicacao) {
                    startActivity(new Intent(Informations.this, Medicine.class));
                    return true;
                }
                return false;
            }
        });
    }

  /*      Button coletor;
        Button monitorar;
        Button reutilizavel;
        Button pilula;
        Button endrometriose;

        ImageButton perfil =  findViewById(R.id.perfil);
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Informacao.this, Anotacoes.class);
                startActivity(intent);
            }
        });

        coletor = findViewById(R.id.coletor);
        coletor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link1));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Erro ao abrir o link.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        monitorar = findViewById(R.id.monitorar);
        monitorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link2));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Erro ao abrir o link.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        reutilizavel = findViewById(R.id.reutilizavel);
        reutilizavel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link3));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Erro ao abrir o link.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        pilula = findViewById(R.id.pilula);
        pilula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link4));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Erro ao abrir o link.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        endrometriose = findViewById(R.id.endrometriose);
        endrometriose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link5));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Erro ao abrir o link.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        ImageButton anota = findViewById(R.id.anota);
        anota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Informacao.this, Anotacoes.class);
                startActivity(intent);
            }
        });

        ImageButton calendario = findViewById(R.id.calendario);
        calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Informacao.this, Calendario.class);
                startActivity(intent);
            }
        });

        ImageButton menu = findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Informacao.this, Notificacoes.class);
                startActivity(intent);
            }
        });

        ImageButton seekbar = findViewById(R.id.seekbar);
        seekbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Informacao.this, LinhaDoTempo.class);
                startActivity(intent);
            }
        });
    } */
}
