package br.edu.fatecsaocaetano.hystera.navegabilidade;

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
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class Graph extends AppCompatActivity {

    private LineChart lineChart;
    private CircularSeekBar seekbarDuration; // CircularSeekBar para duração
    private CircularSeekBar seekbarBleeding; // CircularSeekBar para sangramento
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        // Inicializando o CircularSeekBar
        seekbarDuration = findViewById(R.id.seekbar_duration); // Obtenha a referência
        seekbarBleeding = findViewById(R.id.seekbar_duration1); // Obtenha a referência para sangramento

        // Inicializando a lista de entradas do gráfico
        List<Entry> cycleEntries = new ArrayList<>();

        // Chamando o método para buscar dados do Firestore
        getLastSixCycles(cycleEntries);
    }

    private void getLastSixCycles(List<Entry> cycleEntries) {
        // Referência para a coleção 'Ciclos' no Firestore
        db.collection("Usuarios")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()) // ID do Usuário
                .collection("Ciclos")
                .orderBy("startDate", Query.Direction.DESCENDING) // Ordenar por data, mais recentes primeiro
                .limit(6) // Limitar aos 6 últimos ciclos
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int index = 0;
                        long sumDuration = 0; // Variável para armazenar a soma das durações
                        long sumBleeding = 0; // Variável para armazenar a soma dos sangramentos
                        int count = 0; // Contador para o número de ciclos

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Pegando o campo 'duration' de cada ciclo
                            Long duration = document.getLong("duration");
                            Long bleeding = document.getLong("bleeding"); // Pegando o campo 'bleeding'

                            if (duration != null) {
                                // Adicionando o valor de 'duration' à lista de entradas do gráfico
                                cycleEntries.add(new Entry(index, duration));
                                sumDuration += duration; // Adiciona à soma das durações
                                count++; // Incrementa o contador
                                index++;
                            }

                            if (bleeding != null) {
                                sumBleeding += bleeding; // Adiciona à soma dos sangramentos
                            }
                        }

                        // Calcular a média como um número inteiro
                        int averageDuration = (count > 0) ? (int) (sumDuration / count) : 0;
                        int averageBleeding = (count > 0) ? (int) (sumBleeding / count) : 0; // Calcular média de bleeding

                        // Usar a média na seekbar
                        updateSeekBarWithAverage(averageDuration, averageBleeding); // Passar média de bleeding

                        // Criar e configurar o gráfico após adicionar as entradas
                        setupChart(cycleEntries);
                    } else {
                        Log.e("Firestore", "Erro ao buscar documentos: ", task.getException());
                    }
                });
    }

    private void setupChart(List<Entry> cycleEntries) {
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

        // Configurando o eixo Y para exibir apenas números inteiros
        lineChart.getAxisLeft().setGranularity(1f); // Define a granularidade para 1
        lineChart.getAxisLeft().setAxisMaximum(80f); // Define o máximo do eixo Y
        lineChart.getAxisLeft().setAxisMinimum(0f); // Define o mínimo do eixo Y
        lineChart.getAxisRight().setEnabled(false); // Desabilita o eixo Y da direita

        // Atualiza o gráfico para refletir as configurações
        lineChart.invalidate();
        Log.d("Grafico", "Gráfico atualizado.");

        // Descrição do gráfico
        Description description = new Description();
        description.setText("Duração dos ciclos menstruais");
        lineChart.setDescription(description);
    }


    private void updateSeekBarWithAverage(int averageDuration, int averageBleeding) {
        // Atualiza os CircularSeekBars com a média calculada
        seekbarDuration.setProgress(averageDuration); // Define o progresso da seekbar de duração
        seekbarBleeding.setProgress(averageBleeding); // Define o progresso da seekbar de sangramento

        // Mostrando as médias no TextView
        MaterialTextView resultDuration = findViewById(R.id.result_duration);
        resultDuration.setText(averageDuration + " dias");

        MaterialTextView resultBleeding = findViewById(R.id.result_duration1);
        resultBleeding.setText(averageBleeding + " dias"); // Exibindo média de bleeding
    }
}
