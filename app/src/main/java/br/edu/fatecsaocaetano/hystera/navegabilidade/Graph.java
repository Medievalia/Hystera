package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
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
    private final String tag = "GraficoClass";
    private String userID;
    private Timestamp startDate;
    private Timestamp endDate;
    private FirebaseFirestore db; // Declarado aqui para uso em todo o código
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafic);

        // Inicializando Firestore e o formato de data
        db = FirebaseFirestore.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Inicializando o LineChart
        lineChart = findViewById(R.id.chart);
        if (lineChart == null) {
            Log.e(tag, "LineChart não foi inicializado.");
            return;
        }

        // Verifica se o usuário está autenticado
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            buscarDuracoesCiclos(userID);
        } else {
            Log.e(tag, "Usuário não autenticado!");
            // Exibir mensagem ao usuário se não estiver logado usando Toast
            Toast.makeText(this, "Você precisa estar logado para visualizar os ciclos.", Toast.LENGTH_LONG).show();
        }
    }

    private void buscarDuracoesCiclos(String userID) {
        db.collection("Usuarios")
                .document(userID)
                .collection("Ciclos")
                .orderBy("endDate")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Entry> cycleEntries = new ArrayList<>();
                        int index = 0;

                        QuerySnapshot ciclos = task.getResult();
                        if (ciclos != null) {
                            for (QueryDocumentSnapshot document : ciclos) {
                                // Obter startDate e endDate do documento
                                String startDateString = document.getString("startDate");
                                String endDateString = document.getString("endDate");

                                if (startDateString != null && endDateString != null) {
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
                                        Log.e(tag, "Erro ao parsear datas: ", e);
                                    }
                                } else {
                                    Log.e(tag, "Datas não disponíveis no documento: " + document.getId());
                                }
                            }

                            // Se houver ciclos, atualiza o gráfico
                            if (!cycleEntries.isEmpty()) {
                                atualizarGrafico(cycleEntries);
                            } else {
                                Log.e(tag, "Nenhum ciclo encontrado.");
                                // Exibir uma mensagem ao usuário usando Toast
                                Toast.makeText(this, "Nenhum ciclo encontrado para este usuário.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.e(tag, "Resultado da consulta é nulo.");
                        }

                    } else {
                        Log.e(tag, "Erro ao buscar ciclos: ", task.getException());
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
        Log.d(tag, "Gráfico atualizado.");

        // Descrição do gráfico
        Description description = new Description();
        description.setText("Duração dos ciclos menstruais (em dias)");
        lineChart.setDescription(description);
    }
}
