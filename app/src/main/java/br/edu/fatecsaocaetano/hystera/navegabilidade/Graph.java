package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import br.edu.fatecsaocaetano.hystera.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.components.XAxis;
import com.google.android.material.button.MaterialButton;
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

    private static final String TAG = "Graph"; // Tag para logging
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

        // Inicializando o botão "Voltar"
        voltarButton = findViewById(R.id.voltar_button);
        voltarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Graph.this, TimeLine.class);
                startActivity(intent);
                finish(); // Finaliza a Activity atual (opcional)
            }
        });

        // Inicializando o botão "Perfil"
        perfilButton = findViewById(R.id.button_perfil);
        perfilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Graph.this, Profile.class);
                startActivity(intent);
                finish(); // Finaliza a Activity atual (opcional)
            }
        });

        // Inicializando o LineChart
        lineChart = findViewById(R.id.chart);
        if (lineChart == null) {
            Log.e(TAG, "LineChart não foi inicializado.");
            return;
        }

        // Inicializando o CircularSeekBar
        seekbarDuration = findViewById(R.id.seekbar_duration);
        seekbarBleeding = findViewById(R.id.seekbar_duration1);

        // Definindo o máximo da seekbar de sangramento
        seekbarBleeding.setMax(15);

        // Desabilitando a interação do usuário nas seekBars
        seekbarDuration.setEnabled(false);
        seekbarBleeding.setEnabled(false);

        // Inicializando os TextViews
        resultDuration = findViewById(R.id.result_duration); // Para a média de duração
        resultBleeding = findViewById(R.id.result_duration1); // Para a média de sangramento

        // Inicializando a lista de entradas do gráfico
        List<Entry> cycleEntries = new ArrayList<>();
        List<String> monthLabels = new ArrayList<>();

        // Chamando o método para buscar dados do Firestore
        getLastSixCycles(cycleEntries, monthLabels);
    }

    private void getLastSixCycles(List<Entry> cycleEntries, List<String> monthLabels) {
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

                        Map<String, Integer> monthlyData = new HashMap<>();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date());
                        List<String> allMonths = new ArrayList<>();

                        for (int i = 0; i < 6; i++) {
                            String monthYearLabel = getMonthYearLabel(calendar.getTime());
                            allMonths.add(monthYearLabel);
                            calendar.add(Calendar.MONTH, -1);
                        }

                        // Inicializa os meses no mapa
                        for (String month : allMonths) {
                            monthlyData.put(month, 0);
                        }

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Long duration = document.getLong("duration");
                            Long bleeding = document.getLong("bleeding");
                            Date startDate = document.getDate("startDate");

                            if (duration != null && startDate != null) {
                                String monthYearLabel = getMonthYearLabel(startDate);
                                monthlyData.put(monthYearLabel, monthlyData.get(monthYearLabel) + duration.intValue());
                                sumDuration += duration;
                                count++;
                            } else {
                                Log.w(TAG, "Duração ou data não disponível para o documento: " + document.getId());
                            }

                            if (bleeding != null) {
                                sumBleeding += bleeding;
                            } else {
                                Log.w(TAG, "Sangramento não disponível para o documento: " + document.getId());
                            }
                        }

                        // Adiciona entradas ao gráfico apenas para meses que têm dados
                        for (int index = 0; index < allMonths.size(); index++) {
                            int durationValue = monthlyData.get(allMonths.get(index));
                            if (durationValue > 0) { // Adiciona apenas se a duração for maior que zero
                                cycleEntries.add(new Entry(index, durationValue));
                                monthLabels.add(allMonths.get(index));
                            }
                        }

                        // Logando o total para verificação
                        Log.d(TAG, "Soma de Duração: " + sumDuration + ", Contagem: " + count);
                        Log.d(TAG, "Soma de Sangramento: " + sumBleeding);

                        int averageDuration = (count > 0) ? (int) (sumDuration / count) : 0;
                        int averageBleeding = (count > 0) ? (int) (sumBleeding / count) : 0;

                        Log.d(TAG, "Média de Duração: " + averageDuration);
                        Log.d(TAG, "Média de Sangramento: " + averageBleeding);

                        // Atualizando os TextViews com as médias
                        updateSeekBarWithAverage(averageDuration, averageBleeding);
                        updateResultTextViews(averageDuration, averageBleeding);
                        setupChart(cycleEntries, monthLabels);
                    } else {
                        Log.e(TAG, "Erro ao buscar documentos: ", task.getException());
                    }
                });
    }

    private void updateResultTextViews(int averageDuration, int averageBleeding) {
        // Atualizando os TextViews com as médias
        resultDuration.setText(averageDuration + " dias"); // Atualiza o primeiro TextView (duração)
        resultBleeding.setText(averageBleeding + " dias"); // Atualiza o segundo TextView (sangramento)
    }

    private String getMonthYearLabel(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        String[] months = new String[]{"JAN", "FEV", "MAR", "ABR", "MAI", "JUN", "JUL", "AGO", "SET", "OUT", "NOV", "DEZ"};
        return String.format("%02d %s", month + 1, months[month]);
    }

    private void setupChart(List<Entry> cycleEntries, List<String> monthLabels) {
        Log.d(TAG, "Número de entradas: " + cycleEntries.size());

        LineDataSet lineDataSet = new LineDataSet(cycleEntries, "Últimos 6 ciclos");
        lineDataSet.setColor(Color.parseColor("#FF4081")); // Alterando a cor da linha
        lineDataSet.setValueTextColor(Color.WHITE); // Mudando a cor do texto dos valores para branco
        lineDataSet.setLineWidth(3f); // Aumentando a largura da linha
        lineDataSet.setCircleColor(Color.RED); // Alterando a cor dos círculos
        lineDataSet.setCircleRadius(6f); // Aumentando o tamanho dos círculos
        lineDataSet.setDrawValues(true); // Para mostrar os valores acima dos pontos

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineChart.setVisibility(View.VISIBLE);

        lineChart.getAxisLeft().setGranularity(1f);
        lineChart.getAxisLeft().setAxisMaximum(80f); // Ajuste conforme necessário
        lineChart.getAxisLeft().setAxisMinimum(0f);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(monthLabels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(monthLabels.size());

        Description description = new Description();
        description.setText("Ciclos nos últimos meses");
        lineChart.setDescription(description);

        lineChart.invalidate(); // Atualiza o gráfico
    }

    private void updateSeekBarWithAverage(int averageDuration, int averageBleeding) {
        // Atualizando as seekbars com as médias
        seekbarDuration.setProgress(averageDuration);
        seekbarBleeding.setProgress(averageBleeding);
    }
}
