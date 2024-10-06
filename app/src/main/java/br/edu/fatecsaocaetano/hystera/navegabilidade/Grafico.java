package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class Grafico extends AppCompatActivity {

    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafic);

        // Inicializando o LineChart
        lineChart = findViewById(R.id.chart);
        if (lineChart == null) {
            Log.e("Grafico", "LineChart não foi inicializado.");
            return;
        }

        // Criando dados fictícios para os últimos 6 ciclos
        List<Entry> cycleEntries = new ArrayList<>();
        cycleEntries.add(new Entry(0, 28)); // Ciclo 1: 28 dias
        cycleEntries.add(new Entry(1, 30)); // Ciclo 2: 30 dias
        cycleEntries.add(new Entry(2, 27)); // Ciclo 3: 27 dias
        cycleEntries.add(new Entry(3, 29)); // Ciclo 4: 29 dias
        cycleEntries.add(new Entry(4, 26)); // Ciclo 5: 26 dias
        cycleEntries.add(new Entry(5, 31)); // Ciclo 6: 31 dias

        Log.d("Grafico", "Número de entradas: " + cycleEntries.size());

        // Criando o LineDataSet e configurando sua aparência
        LineDataSet lineDataSet = new LineDataSet(cycleEntries, "Últimos 6 ciclos");
        lineDataSet.setColor(Color.parseColor("#8A50FF")); // Cor da linha
        lineDataSet.setValueTextColor(Color.BLACK); // Cor do texto dos valores
        lineDataSet.setLineWidth(2f); // Espessura da linha
        lineDataSet.setCircleColor(Color.BLACK); // Cor dos círculos nos pontos de dados
        lineDataSet.setCircleRadius(5f); // Tamanho dos círculos

        // Criando o LineData com o dataset
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineChart.setVisibility(View.VISIBLE); // Garante que o gráfico esteja visível

        // Atualiza e valida o gráfico
        lineChart.invalidate();
        Log.d("Grafico", "Gráfico atualizado.");

        // Descrição do gráfico
        Description description = new Description();
        description.setText("Duração dos ciclos menstruais");
        lineChart.setDescription(description);
    }
}
