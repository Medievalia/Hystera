package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import br.edu.fatecsaocaetano.hystera.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Graph extends AppCompatActivity {

    private LineChart lineChart;
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // Conectando ao Firestore
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
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Pegando o campo 'duration' de cada ciclo
                            Long duration = document.getLong("duration");
                            if (duration != null) {
                                // Adicionando o valor de 'duration' à lista de entradas do gráfico
                                cycleEntries.add(new Entry(index, duration));
                                index++;
                            }
                        }

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

        // Atualiza e valida o gráfico
        lineChart.invalidate();
        Log.d("Grafico", "Gráfico atualizado.");

        // Descrição do gráfico
        Description description = new Description();
        description.setText("Duração dos ciclos menstruais");
        lineChart.setDescription(description);
    }
}
