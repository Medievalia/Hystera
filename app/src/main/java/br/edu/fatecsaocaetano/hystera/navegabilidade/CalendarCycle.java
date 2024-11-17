package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import br.edu.fatecsaocaetano.hystera.R;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

public class CalendarCycle extends AppCompatActivity {

    private static final String tag = "CalendarCycleClass";
    private MaterialCalendarView currentMonthCalendarView;
    private TextView txtMonth;
    private MaterialButton btn_graph;
    private MaterialButton perfilButton;
    private String userID;
    private Cycle currentCycle;
    private Calendar displayedCalendar;
    private List<Cycle> ciclosAnteriores = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        currentMonthCalendarView = findViewById(R.id.currentMonthCalendarView);
        txtMonth = findViewById(R.id.txt_month);
        btn_graph = findViewById(R.id.button_gafico);
        perfilButton = findViewById(R.id.button_perfil);
        displayedCalendar = Calendar.getInstance();

        // Navegação e menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationHelper menuHelper = new BottomNavigationHelper();
        menuHelper.setNavigationFocus(bottomNavigationView, R.id.nav_calendario);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_anota) {
                    startActivity(new Intent(CalendarCycle.this, Notes.class));
                    startActivity(new Intent(CalendarCycle.this, Annotations.class));
                    return true;
                } else if (id == R.id.nav_calendario) {
                    startActivity(new Intent(CalendarCycle.this, CalendarCycle.class));
                    return true;
                } else if (id == R.id.nav_utero) {
                    startActivity(new Intent(CalendarCycle.this, Informations.class));
                    return true;
                } else if (id == R.id.nav_seekbar) {
                    startActivity(new Intent(CalendarCycle.this, TimeLine.class));
                    return true;
                } else if (id == R.id.nav_medicacao) {
                    startActivity(new Intent(CalendarCycle.this, Medicine.class));
                    return true;
                }
                return false;
            }
        });

        try {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            recuperarCiclos();
        } catch (NullPointerException e) {
            Log.e(tag, "ID do usuário não encontrado!", e);
            return;
        }

        txtMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navegarParaProximoMes();
            }
        });

        perfilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarCycle.this, Profile.class);
                startActivity(intent);
            }
        });

        btn_graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarCycle.this, Graph.class);
                startActivity(intent);
            }
        });
    }

    private void recuperarCiclos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Usuarios")
                .document(userID)
                .collection("Ciclos")
                .orderBy("startDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            Cycle ciclo = documentSnapshot.toObject(Cycle.class);
                            Timestamp startDate = Timestamp.now();
                            if (ciclo != null) {
                                Map<String, Map<String, Timestamp>> fases = (Map<String, Map<String, Timestamp>>) documentSnapshot.get("fases");
                                if (fases != null) {
                                    startDate = ajustarDataPara3HorasAntes(documentSnapshot.getTimestamp("startDate"));
                                    ciclo.setStartDate(startDate);

                                    Map<String, Timestamp> faseMenstrual = fases.get("Menstrual");
                                    if (faseMenstrual != null) {
                                        Timestamp inicioMenstrual = ajustarDataPara3HorasAntes(faseMenstrual.get("inicio"));
                                        Timestamp fimMenstrual = ajustarDataPara3HorasAntes(faseMenstrual.get("fim"));
                                        ciclo.adicionarFase("Menstrual", inicioMenstrual, fimMenstrual);
                                    }

                                    Map<String, Timestamp> faseFolicular = fases.get("Folicular");
                                    if (faseFolicular != null) {
                                        Timestamp inicioFolicular = ajustarDataPara3HorasAntes(faseFolicular.get("inicio"));
                                        Timestamp fimFolicular = ajustarDataPara3HorasAntes(faseFolicular.get("fim"));
                                        ciclo.adicionarFase("Folicular", inicioFolicular, fimFolicular);
                                    }

                                    Map<String, Timestamp> faseOvulacao = fases.get("Ovulacao");
                                    if (faseOvulacao != null) {
                                        Timestamp inicioOvulacao = ajustarDataPara3HorasAntes(faseOvulacao.get("inicio"));
                                        Timestamp fimOvulacao = ajustarDataPara3HorasAntes(faseOvulacao.get("fim"));
                                        ciclo.setOvulation(inicioOvulacao);
                                        ciclo.adicionarFase("Ovulacao", inicioOvulacao, fimOvulacao);
                                    }

                                    Map<String, Timestamp> faseLutea = fases.get("Lutea");
                                    if (faseLutea != null) {
                                        Timestamp inicioLutea = ajustarDataPara3HorasAntes(faseLutea.get("inicio"));
                                        Timestamp fimLutea = ajustarDataPara3HorasAntes(faseLutea.get("fim"));
                                        ciclo.adicionarFase("Lutea", inicioLutea, fimLutea);
                                    }
                                }
                                ciclo.setStartDate(startDate);
                                ciclo.setDuration(documentSnapshot.getLong("duration").intValue());
                                ciclo.setNatural(documentSnapshot.getBoolean("natural"));
                                ciclo.setBleeding(documentSnapshot.getLong("bleeding").intValue());
                                ciclo.setUserID(userID);
                                ciclo.setId(documentSnapshot.getId());
                            }
                            ciclosAnteriores.add(ciclo);
                        }

                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot actualCycle = queryDocumentSnapshots.getDocuments().get(0);
                            currentCycle = actualCycle.toObject(Cycle.class);
                            if (currentCycle != null) {
                                Map<String, Map<String, Timestamp>> fases = (Map<String, Map<String, Timestamp>>) actualCycle.get("fases");
                                if (fases != null) {
                                    Map<String, Timestamp> faseMenstrual = fases.get("Menstrual");
                                    if (faseMenstrual != null) {
                                        Timestamp inicioMenstrual = faseMenstrual.get("inicio");
                                        currentCycle.setStartDate(inicioMenstrual);
                                    }

                                    Map<String, Timestamp> faseOvulacao = fases.get("Ovulacao");
                                    if (faseOvulacao != null) {
                                        Timestamp inicioOvulacao = faseOvulacao.get("inicio");
                                        currentCycle.setOvulation(inicioOvulacao);
                                    }
                                }

                                if (actualCycle.get("endDate") != null) {
                                    Timestamp endDate = (Timestamp) actualCycle.get("endDate");
                                    currentCycle.setEndDate(endDate);
                                }

                                if (actualCycle.getLong("bleeding") != null) {
                                    currentCycle.setBleeding(actualCycle.getLong("bleeding").intValue());
                                }
                            }
                            atualizarCalendario();
                        }
                    } else {
                        Log.e(tag, "Nenhum ciclo encontrado.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(tag, "Erro ao recuperar ciclos.", e);
                });
    }

    private void navegarParaProximoMes() {
        displayedCalendar.add(Calendar.MONTH, 1);
        atualizarCalendario();
    }

    private void atualizarCalendario() {
        currentMonthCalendarView.removeDecorators();

        // Renderizando ciclos anteriores
        for (Cycle ciclo : ciclosAnteriores) {
            Calendar cicloStart = Calendar.getInstance();
            Calendar cicloEnd = Calendar.getInstance();
            cicloStart.setTime(ciclo.getStartDate().toDate());
            cicloEnd.setTime(ciclo.getEndDate().toDate());
            ciclo.marcarDiasNoCalendario(currentMonthCalendarView, cicloStart, cicloEnd, ciclo);
        }

        // Renderizando ciclo atual
        if (currentCycle != null && currentCycle.getStartDate() != null && currentCycle.getEndDate() != null) {
            Calendar cicloStart = Calendar.getInstance();
            Calendar cicloEnd = Calendar.getInstance();
            cicloStart.setTime(currentCycle.getStartDate().toDate());
            cicloEnd.setTime(currentCycle.getEndDate().toDate());
            currentCycle.marcarDiasNoCalendario(currentMonthCalendarView, cicloStart, cicloEnd, currentCycle);
        }

        // Renderizando ciclos futuros
        if (currentCycle != null) {
            Calendar nextStart = Calendar.getInstance();
            Calendar nextEnd = Calendar.getInstance();
            Cycle proximoCiclo = currentCycle;

            for (int i = 1; i <= 12; i++) {
                proximoCiclo = proximoCiclo.simularProximoCiclo(proximoCiclo);
                nextStart.setTime(proximoCiclo.getStartDate().toDate());
                nextEnd.setTime(proximoCiclo.getEndDate().toDate());
                proximoCiclo.marcarDiasNoCalendario(currentMonthCalendarView, nextStart, nextEnd, proximoCiclo);
            }
        }
    }

    private Timestamp ajustarDataPara3HorasAntes(Timestamp timestamp) {
        if (timestamp != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(timestamp.toDate());
            calendar.add(Calendar.HOUR_OF_DAY, -3);
            return new Timestamp(calendar.getTime());
        }
        return null;
    }
}
