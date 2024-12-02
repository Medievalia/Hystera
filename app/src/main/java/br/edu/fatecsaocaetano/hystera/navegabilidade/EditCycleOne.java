package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import br.edu.fatecsaocaetano.hystera.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditCycleOne extends AppCompatActivity {

    private String userID;
    private final String tag = "DUMClass";
    private Timestamp dataSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editarciclo2);
        AppCompatButton seguinte = findViewById(R.id.btn_seguinte);
        AppCompatButton nao_sei = findViewById(R.id.nao_sei);
        MaterialCalendarView calendarView = findViewById(R.id.calendarView);

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                // Salvar a data como Timestamp
                dataSelecionada = getTimestamp(date.getYear(), date.getMonth(), date.getDay() + 1);
            }
        });

        seguinte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataSelecionada != null) {
                    interromperCiclosEAtualizarDUM(dataSelecionada);
                } else {
                    Log.e(tag, "Nenhuma data foi selecionada.");
                }
            }
        });

        nao_sei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditCycleOne.this, EditCycleTwo.class);
                startActivity(intent);
            }
        });


        AppCompatButton voltar = findViewById(R.id.back_button);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Método para marcar os ciclos como "interrompidos" e salvar a data DUM
    private void interromperCiclosEAtualizarDUM(Timestamp dataSelecionada) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Obter o userID
        try {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException e) {
            Log.e(tag, "ID do usuário não encontrado!");
            return;
        }

        // Iniciar um batch para atualizar os ciclos e a atualização do DUM
        WriteBatch batch = db.batch();

        // Referência para a coleção "Ciclos" do usuário
        Query ciclosQuery = db.collection("Usuarios").document(userID).collection("Ciclos");
        ciclosQuery.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Para cada ciclo encontrado, atualizamos o campo "interrupted" para true
                        Map<String, Object> dadosParaAtualizar = new HashMap<>();
                        dadosParaAtualizar.put("notCorrect", true); // Marcando o ciclo como interrompido
                        batch.update(documentSnapshot.getReference(), dadosParaAtualizar);
                    }

                    // Agora vamos salvar o DUM do usuário
                    Map<String, Object> dadosParaAtualizarDUM = new HashMap<>();
                    dadosParaAtualizarDUM.put("DUM", dataSelecionada);
                    DocumentReference documentReference = db.collection("Usuarios").document(userID);
                    batch.set(documentReference, dadosParaAtualizarDUM, SetOptions.merge());

                    // Commitando o batch
                    batch.commit()
                            .addOnSuccessListener(aVoid -> {
                                Log.i(tag, "Ciclos marcados como interrompidos e DUM atualizado com sucesso!");
                                Intent intent = new Intent(EditCycleOne.this, EditCycleTwo.class);
                                startActivity(intent);
                            })
                            .addOnFailureListener(e -> Log.e(tag, "Erro ao interromper ciclos ou atualizar DUM.", e));
                })
                .addOnFailureListener(e -> Log.e(tag, "Erro ao obter ciclos do usuário.", e));
    }

    private Timestamp getTimestamp(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTime());
    }
}
