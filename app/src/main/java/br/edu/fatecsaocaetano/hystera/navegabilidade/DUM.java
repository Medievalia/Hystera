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
import com.google.firebase.firestore.SetOptions;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DUM extends AppCompatActivity {

    private String userID;
    private final String tag = "DUMClass";
    private Timestamp dataSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_2);
        AppCompatButton seguinte = findViewById(R.id.btn_seguinte);
        MaterialCalendarView calendarView = findViewById(R.id.calendarView);

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                // Salvar a data como Timestamp
                dataSelecionada = getTimestamp(date.getYear(), date.getMonth(), date.getDay());
            }
        });

        seguinte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataSelecionada != null) {
                    salvarDUM(dataSelecionada);
                } else {
                    Log.e(tag, "Nenhuma data foi selecionada.");
                }
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

    private void salvarDUM(Timestamp dataSelecionada) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> dadosParaAtualizar = new HashMap<>();
        dadosParaAtualizar.put("DUM", dataSelecionada);

        try {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException e) {
            Log.e(tag, "ID do usuário não encontrado!");
        }

        DocumentReference documentReference = db.collection("Usuarios").document(userID);

        documentReference.set(dadosParaAtualizar, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(tag, "Data da última menstruação salva com sucesso!");
                        Intent intent = new Intent(DUM.this, FirstCycle.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(tag, "Erro ao salvar a data da última menstruação.", e);
                    }
                });
    }

    private Timestamp getTimestamp(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTime());
    }
}
