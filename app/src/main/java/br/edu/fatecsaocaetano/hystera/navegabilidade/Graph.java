package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import br.edu.fatecsaocaetano.hystera.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.tankery.lib.circularseekbar.CircularSeekBar;

public class Graph extends AppCompatActivity {

    private static final String tag = "GraphClass"; // Tag para logging
    private LineChart lineChart;
    private MaterialButton voltarButton;
    private MaterialButton perfilButton;
    private CircularSeekBar seekbarDuration; // CircularSeekBar para duração
    private CircularSeekBar seekbarBleeding; // CircularSeekBar para sangramento
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Declarando os TextViews para mostrar os resultados
    private MaterialTextView resultDuration; // Para o primeiro TextView (duração)
    private MaterialTextView resultBleeding; // Para o segundo TextView (sangramento)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafic);

        voltarButton = findViewById(R.id.voltar_button);
        voltarButton.setOnClickListener(v -> finish());

        perfilButton = findViewById(R.id.button_perfil);
        perfilButton.setOnClickListener(v -> {
            Intent intent = new Intent(Graph.this, Profile.class);
            startActivity(intent);
        });

        lineChart = findViewById(R.id.chart);
        lineChart.setBackgroundColor(Color.WHITE);
        if (lineChart == null) {
            Log.e(tag, "LineChart não foi inicializado.");
            return;
        }

        seekbarDuration = findViewById(R.id.seekbar_duration);
        seekbarBleeding = findViewById(R.id.seekbar_duration1);
        seekbarBleeding.setMax(15);
        seekbarDuration.setEnabled(false);
        seekbarBleeding.setEnabled(false);

        resultDuration = findViewById(R.id.result_duration);
        resultBleeding = findViewById(R.id.result_duration1);

        List<Entry> cycleEntries = new ArrayList<>();
        List<Entry> bleedingEntries = new ArrayList<>();
        List<String> monthLabels = new ArrayList<>();

        getLastSixCycles(cycleEntries, bleedingEntries, monthLabels);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationHelper menuHelper = new BottomNavigationHelper();
        menuHelper.setNavigationFocus(bottomNavigationView, R.id.nav_seekbar);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_anota) {
                    startActivity(new Intent(Graph.this, Notes.class));
                    startActivity(new Intent(Graph.this, Annotations.class));
                    return true;
                } else if (id == R.id.nav_calendario) {
                    startActivity(new Intent(Graph.this, CalendarCycle.class));
                    return true;
                } else if (id == R.id.nav_utero) {
                    startActivity(new Intent(Graph.this, Informations.class));
                    return true;
                } else if (id == R.id.nav_seekbar) {
                    startActivity(new Intent(Graph.this, TimeLine.class));
                    return true;
                } else if (id == R.id.nav_medicacao) {
                    startActivity(new Intent(Graph.this, Medicine.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void getLastSixCycles(List<Entry> cycleEntries, List<Entry> bleedingEntries, List<String> monthLabels) {
        db.collection("Usuarios")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Ciclos")
                .orderBy("startDate", Query.Direction.ASCENDING)
                .limit(6)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        long sumDuration = 0;
                        long sumBleeding = 0;
                        int count = 0;

                        Map<String, Integer> monthlyDataDuration = new HashMap<>();
                        Map<String, Integer> monthlyDataBleeding = new HashMap<>();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date());
                        List<String> allMonths = new ArrayList<>();

                        for (int i = 0; i < 6; i++) {
                            String monthYearLabel = getMonthYearLabel(calendar.getTime());
                            allMonths.add(monthYearLabel);
                            calendar.add(Calendar.MONTH, -1);
                        }

                        for (String month : allMonths) {
                            monthlyDataDuration.put(month, 0);
                            monthlyDataBleeding.put(month, 0);
                        }

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Boolean notCorrect = document.getBoolean("notCorrect");
                            if (notCorrect != null && notCorrect) {
                                continue;
                            }

                            Long duration = document.getLong("duration");
                            Long bleeding = document.getLong("bleeding");
                            Date startDate = document.getDate("startDate");

                            if (duration != null && startDate != null) {
                                String monthYearLabel = getMonthYearLabel(startDate);
                                monthlyDataDuration.put(monthYearLabel, monthlyDataDuration.get(monthYearLabel) + duration.intValue());
                                sumDuration += duration;
                                count++;
                            } else {
                                Log.w(tag, "Duração ou data não disponível para o documento: " + document.getId());
                            }

                            if (bleeding != null) {
                                String monthYearLabel = getMonthYearLabel(startDate);
                                monthlyDataBleeding.put(monthYearLabel, monthlyDataBleeding.get(monthYearLabel) + bleeding.intValue());
                                sumBleeding += bleeding;
                            } else {
                                Log.w(tag, "Sangramento não disponível para o documento: " + document.getId());
                            }
                        }

                        for (int index = 0; index < allMonths.size(); index++) {
                            int durationValue = monthlyDataDuration.get(allMonths.get(index));
                            int bleedingValue = monthlyDataBleeding.get(allMonths.get(index));
                            if (durationValue > 0 || bleedingValue > 0) {
                                cycleEntries.add(new Entry(index, durationValue));
                                bleedingEntries.add(new Entry(index, bleedingValue));
                                monthLabels.add(allMonths.get(index));
                            }
                        }

                        Log.d(tag, "Soma de Duração: " + sumDuration + ", Contagem: " + count);
                        Log.d(tag, "Soma de Sangramento: " + sumBleeding);

                        int averageDuration = (count > 0) ? (int) (sumDuration / count) : 0;
                        int averageBleeding = (count > 0) ? (int) (sumBleeding / count) : 0;

                        updateSeekBarWithAverage(averageDuration, averageBleeding);
                        updateResultTextViews(averageDuration, averageBleeding);
                        setupChart(cycleEntries, bleedingEntries, monthLabels);
                    } else {
                        Log.e(tag, "Erro ao buscar documentos: ", task.getException());
                    }
                });
    }

    private void updateResultTextViews(int averageDuration, int averageBleeding) {
        resultDuration.setText(averageDuration + " dias");
        resultBleeding.setText(averageBleeding + " dias");
    }

    private String getMonthYearLabel(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        String[] months = new String[]{"JAN", "FEV", "MAR", "ABR", "MAI", "JUN", "JUL", "AGO", "SET", "OUT", "NOV", "DEZ"};
        return String.format("%02d %s", month + 1, months[month]);
    }

    private void setupChart(List<Entry> cycleEntries, List<Entry> bleedingEntries, List<String> monthLabels) {
        LineDataSet lineDataSetDuration = new LineDataSet(cycleEntries, "Duração");
        lineDataSetDuration.setColor(Color.parseColor("#FFBB86FC"));
        lineDataSetDuration.setCircleColor(Color.parseColor("#800080"));
        lineDataSetDuration.setLineWidth(2f);
        lineDataSetDuration.setCircleRadius(4f);
        lineDataSetDuration.setDrawCircleHole(true);
        lineDataSetDuration.setValueTextSize(12f);
        lineDataSetDuration.setValueTextColor(Color.BLACK);

        LineDataSet lineDataSetBleeding = new LineDataSet(bleedingEntries, "Sangramento");
        lineDataSetBleeding.setColor(Color.RED);
        lineDataSetBleeding.setCircleColor(Color.RED);
        lineDataSetBleeding.setLineWidth(2f);
        lineDataSetBleeding.setCircleRadius(4f);
        lineDataSetBleeding.setDrawCircleHole(true);
        lineDataSetBleeding.setValueTextSize(12f);
        lineDataSetBleeding.setValueTextColor(Color.BLACK);

        lineDataSetDuration.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPointLabel(Entry entry) {
                return String.valueOf((int) entry.getY());
            }
        });

        lineDataSetBleeding.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPointLabel(Entry entry) {
                return String.valueOf((int) entry.getY());
            }
        });

        LineData lineData = new LineData(lineDataSetDuration, lineDataSetBleeding);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawLabels(false); // Desabilita os rótulos do eixo X
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        YAxis leftAxis = lineChart.getAxisLeft();
        YAxis rightAxis = lineChart.getAxisRight();
        leftAxis.setDrawGridLines(false);
        rightAxis.setEnabled(false);

        Description description = new Description();
        description.setText("Histórico de Ciclos");
        description.setTextSize(14f);
        lineChart.setDescription(description);
        lineChart.animateX(1000);
    }

    private void updateSeekBarWithAverage(int averageDuration, int averageBleeding) {
        seekbarDuration.setProgress(averageDuration);
        seekbarBleeding.setProgress(averageBleeding);
    }
}
