package br.edu.fatecsaocaetano.hystera.navegabilidade;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.github.mikephil.charting.data.Entry;

import br.edu.fatecsaocaetano.hystera.R;


public class Graph extends AppCompatActivity {

    private LineChart lineChart;
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // Inicializando Firestore
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // Formato da data
    private Cycle cycle = new Cycle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Chegou aqui !!!!!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafic);

        // Inicializando o LineChart
        lineChart = findViewById(R.id.chart);
        if (lineChart == null) {
            Log.e("Grafico", "LineChart não foi inicializado.");
            return;
        }

        // Chama o método para buscar os dados do Firestore
        buscarDuracoesCiclos(cycle.getUserID());
    }

    private void buscarDuracoesCiclos(String userID) {
        db.collection("Usuarios")
                .document(userID)
                .collection("Ciclos")
                .orderBy("endDate") // Ordena por data de fim, caso você queira os mais recentes
                .get() // Remove o limite para pegar todos os ciclos disponíveis
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Entry> cycleEntries = new ArrayList<>();
                        int index = 0;

                        QuerySnapshot ciclos = task.getResult();
                        for (QueryDocumentSnapshot document : ciclos) {
                            // Obter startDate e endDate do documento
                            String startDateString = document.getString("startDate");
                            String endDateString = document.getString("endDate");

                            try {
                                // Converte as strings de data para objetos Date
                                Date startDate = dateFormat.parse(startDateString);
                                Date endDate = dateFormat.parse(endDateString);

                                // Calcula a diferença em milissegundos e converte para dias
                                if (startDate != null && endDate != null) {
                                    long diffInMillis = endDate.getTime() - startDate.getTime();
                                    int duration = (int) (diffInMillis / (1000 * 60 * 60 * 24)); // Duração em dias

                                    cycleEntries.add(new Entry(index, duration)); // Adiciona a duração no gráfico
                                    index++;
                                }
                            } catch (ParseException e) {
                                Log.e("Grafico", "Erro ao parsear datas: ", e);
                            }
                        }

                        // Se houver ciclos, atualiza o gráfico
                        if (!cycleEntries.isEmpty()) {
                            atualizarGrafico(cycleEntries);
                        } else {
                            Log.e("Grafico", "Nenhum ciclo encontrado.");
                            // Exibir uma mensagem ao usuário
                            //TextView mensagemTexto = findViewById(R.id.mensagem_texto);
                            //mensagemTexto.setText("Nenhum ciclo encontrado para este usuário.");
                            //mensagemTexto.setVisibility(View.VISIBLE); // Torna a mensagem visível
                        }

                    } else {
                        Log.e("Grafico", "Erro ao buscar ciclos: ", task.getException());
                    }
                });
    }

    private void atualizarGrafico(List<Entry> cycleEntries) {
        // Criando o LineDataSet e configurando sua aparência
        LineDataSet lineDataSet = new LineDataSet(cycleEntries, "Ciclos");
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
        description.setText("Duração dos ciclos menstruais (em dias)");
        lineChart.setDescription(description);
    }
}
