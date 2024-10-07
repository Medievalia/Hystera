package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.widget.CalendarView;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
public class DUM extends AppCompatActivity {
    private String userID;
    private static final Logger logger = LoggerUtils.configurarLogger(DUM.class.getName());

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_2);

        ImageButton voltar = findViewById(R.id.back_button);
        AppCompatButton seguinte = findViewById(R.id.btn_seguinte);
        CalendarView calendarView = findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Salvar a data como Timestamp
                Timestamp dataSelecionada = getTimestamp(year, month, dayOfMonth);
                salvarDUM(dataSelecionada);
            }
        });

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DUM.this, MethodUse.class);
                startActivity(intent);
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
            logger.log(Level.SEVERE, "ID do usuário não encontrado!");
        }

        DocumentReference documentReference = db.collection("Usuarios").document(userID);

        documentReference.set(dadosParaAtualizar, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "Data da última menstruação salva com sucesso!");
                        logger.log(Level.INFO, "Data da última menstruação salva com sucesso! " + userID);
                        Intent intent = new Intent(DUM.this, FirstCycle.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "Erro ao salvar a data da última menstruação.", e);
                        logger.log(Level.SEVERE, "Erro ao salvar a data da última menstruação. " + userID + " " + e);
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