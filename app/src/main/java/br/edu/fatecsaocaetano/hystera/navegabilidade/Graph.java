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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
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
            Log.e(tag, "LineChart não foi inicializado.");
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

        // Inicializando as listas de entradas do gráfico
        List<Entry> cycleEntries = new ArrayList<>();
        List<Entry> bleedingEntries = new ArrayList<>();
        List<String> monthLabels = new ArrayList<>();

        // Chamando o método para buscar dados do Firestore
        getLastSixCycles(cycleEntries, bleedingEntries, monthLabels);
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

                        // Inicializa os meses no mapa
                        for (String month : allMonths) {
                            monthlyDataDuration.put(month, 0);
                            monthlyDataBleeding.put(month, 0);
                        }

                        for (QueryDocumentSnapshot document : task.getResult()) {
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

                        // Adiciona entradas ao gráfico apenas para meses que têm dados
                        for (int index = 0; index < allMonths.size(); index++) {
                            int durationValue = monthlyDataDuration.get(allMonths.get(index));
                            int bleedingValue = monthlyDataBleeding.get(allMonths.get(index));
                            if (durationValue > 0 || bleedingValue > 0) { // Adiciona apenas se a duração ou sangramento for maior que zero
                                cycleEntries.add(new Entry(index, durationValue));
                                bleedingEntries.add(new Entry(index, bleedingValue));
                                monthLabels.add(allMonths.get(index));
                            }
                        }

                        // Logando o total para verificação
                        Log.d(tag, "Soma de Duração: " + sumDuration + ", Contagem: " + count);
                        Log.d(tag, "Soma de Sangramento: " + sumBleeding);

                        int averageDuration = (count > 0) ? (int) (sumDuration / count) : 0;
                        int averageBleeding = (count > 0) ? (int) (sumBleeding / count) : 0;

                        Log.d(tag, "Média de Duração: " + averageDuration);
                        Log.d(tag, "Média de Sangramento: " + averageBleeding);

                        // Atualizando os TextViews com as médias
                        updateSeekBarWithAverage(averageDuration, averageBleeding);
                        updateResultTextViews(averageDuration, averageBleeding);
                        setupChart(cycleEntries, bleedingEntries, monthLabels);
                    } else {
                        Log.e(tag, "Erro ao buscar documentos: ", task.getException());
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

    private void setupChart(List<Entry> cycleEntries, List<Entry> bleedingEntries, List<String> monthLabels) {
        Log.d(tag, "Número de entradas: " + cycleEntries.size());

        // Configurando o gráfico para a duração (linha roxa)
        LineDataSet lineDataSetDuration = new LineDataSet(cycleEntries, "Duração");
        lineDataSetDuration.setColor(Color.parseColor("#FFBB86FC")); // Linha roxa
        lineDataSetDuration.setCircleColor(Color.parseColor("#800080"));
        lineDataSetDuration.setLineWidth(2f);
        lineDataSetDuration.setCircleRadius(4f);
        lineDataSetDuration.setDrawCircleHole(true);
        lineDataSetDuration.setValueTextSize(12f);
        lineDataSetDuration.setValueTextColor(Color.BLACK);

        // Configurando o gráfico para o sangramento (linha vermelha)
        LineDataSet lineDataSetBleeding = new LineDataSet(bleedingEntries, "Sangramento");
        lineDataSetBleeding.setColor(Color.RED); // Linha vermelha
        lineDataSetBleeding.setCircleColor(Color.RED);
        lineDataSetBleeding.setLineWidth(2f);
        lineDataSetBleeding.setCircleRadius(4f);
        lineDataSetBleeding.setDrawCircleHole(true);
        lineDataSetBleeding.setValueTextSize(12f);
        lineDataSetBleeding.setValueTextColor(Color.BLACK);

        // Usando ValueFormatter personalizado para mostrar os valores inteiros
        lineDataSetDuration.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPointLabel(Entry entry) {
                return String.valueOf((int) entry.getY()); // Exibe o valor como inteiro
            }
        });

        lineDataSetBleeding.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPointLabel(Entry entry) {
                return String.valueOf((int) entry.getY()); // Exibe o valor como inteiro
            }
        });

        // Configurando os dados do gráfico
        LineData lineData = new LineData(lineDataSetDuration, lineDataSetBleeding);
        lineChart.setData(lineData);

        // Configurando o eixo X com os nomes dos meses
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(monthLabels));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);
        xAxis.setTextSize(12f);
        xAxis.setAxisMinimum(0f); // Define o valor mínimo do eixo X
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setDrawGridLines(false); // Desabilita as linhas de grade do eixo X
        xAxis.setLabelCount(monthLabels.size(), true); // Certifique-se de que há um rótulo para cada mês

        // Adicionando rótulo ao eixo X
        xAxis.setDrawLabels(true);
        lineChart.getXAxis().setLabelCount(monthLabels.size());

        // Configurando o eixo Y (durante e sangramento em dias)
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setLabelCount(6, true);
        leftAxis.setDrawLabels(true);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setAxisLineColor(Color.BLACK);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setTextSize(12f);
        leftAxis.setAxisMinimum(0f); // Definir o mínimo de dias como 0
        leftAxis.setGranularity(1f); // Define a granularidade para mostrar os números inteiros
        leftAxis.setDrawGridLines(true); // Exibe as linhas de grade no eixo Y

        // Oculta o eixo Y da direita
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Removendo a descrição do gráfico
        Description description = new Description();
        description.setText(""); // Define o texto da descrição como vazio
        lineChart.setDescription(description);

        // Atualizando o gráfico
        lineChart.invalidate(); // Redesenha o gráfico
    }

    private void updateSeekBarWithAverage(int averageDuration, int averageBleeding) {
        // Atualiza os CircularSeekBars com as médias
        seekbarDuration.setProgress(averageDuration);
        seekbarBleeding.setProgress(averageBleeding);
    }
}
