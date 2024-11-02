package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import br.edu.fatecsaocaetano.hystera.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.Calendar;
import java.util.Map;
import me.tankery.lib.circularseekbar.CircularSeekBar;

public class TimeLine extends AppCompatActivity {

    private final String tag = "LinhaDoTempoClass";
    private String userID;
    private CircularSeekBar seekBar;
    private CircularSeekBar seekBarPeriod;
    private CircularSeekBar seekBarNextBleeding;
    private Cycle currentCycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linha_tempo);
        seekBar = findViewById(R.id.seekbar);
        seekBarPeriod = findViewById(R.id.seekbar_period);
        seekBarNextBleeding = findViewById(R.id.seekbar_next);

        // Desabilitando a interação com as seekbars
        seekBar.setEnabled(false);
        seekBarPeriod.setEnabled(false);
        seekBarNextBleeding.setEnabled(false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException e) {
            Log.e(tag, "ID do usuário não encontrado!");
            return;
        }

        db.collection("Usuarios")
                .document(userID)
                .collection("Ciclos")
                .orderBy("startDate", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot actualCycle = queryDocumentSnapshots.getDocuments().get(0);
                        Cycle ultimoCiclo = actualCycle.toObject(Cycle.class);

                        if (ultimoCiclo != null) {
                            if (isCicloAtualTerminado(ultimoCiclo)) {
                                currentCycle = ultimoCiclo.simularProximoCiclo(ultimoCiclo);
                                currentCycle.salvarCicloNoFirebase();
                            } else {
                                currentCycle = ultimoCiclo;
                            }
                        } else {
                            Log.e(tag, "Ciclo nullo para usuário: " + userID);
                        }
                        atualizandoSeekBar(currentCycle);
                    } else {
                        Log.e(tag, "Nenhum ciclo encontrado.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(tag, "Erro ao recuperar o último ciclo. ", e);
                });

        carregandoBotoesAppCompatButton(R.id.button_grafico, Graph.class);
        carregandoBotoesAppCompatButton(R.id.button_agendamento, Scheduling.class);
        carregandoBotoesAppCompatButton(R.id.button_perfil, Profile.class);
        carregandoBotoesAppCompatImageButton(R.id.button_menstruacao, NewBleeding.class);
        carregandoBotoesAppCompatImageButton(R.id.button_periodo, PeriodCycle.class);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_anota) {
                    startActivity(new Intent(TimeLine.this, Annotations.class));
                    return true;
                } else if (id == R.id.nav_calendario) {
                    startActivity(new Intent(TimeLine.this, CalendaryCycle.class));
                    return true;
                } else if (id == R.id.nav_utero) {
                    startActivity(new Intent(TimeLine.this, Informations.class));
                    return true;
                } else if (id == R.id.nav_seekbar) {
                    startActivity(new Intent(TimeLine.this, TimeLine.class));
                    return true;
                } else if (id == R.id.nav_medicacao) {
                    startActivity(new Intent(TimeLine.this, Medicine.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void atualizandoSeekBar(Cycle currentCycle) {
        Map<String, String> informacoesCiclo = currentCycle.obterInformacoesDoCicloAtual();

        Log.i(tag, "Iniciando atualização dos componentes SeekBar " + userID);
        int duracaoCiclo = currentCycle.getDuration();
        String nomeFase = informacoesCiclo.get("faseAtual");
        String diaDoCiclo = informacoesCiclo.get("diaDoCiclo");

        Log.i(tag, "Fase atual do ciclo: " + nomeFase + ", " + userID);
        Log.i(tag, "Dia do ciclo: " + diaDoCiclo + "º, " + userID);

        MaterialTextView resultDuration = findViewById(R.id.result_duration);
        if (nomeFase.equals("Ovulacao")) {
            resultDuration.setText("Fase Ovulatória");
        } else {
            resultDuration.setText(String.format("Fase %s", nomeFase));
        }

        Map<String, Integer> diasRestantes = currentCycle.calcularDiasRestantes();
        int diasProximaMenstruacao = diasRestantes.get("diasProximaMenstruacao") + 1;
        int diasProximaFase = diasRestantes.get("diasProximaFase") - 1;

        MaterialTextView resultDuration1 = findViewById(R.id.result_duration1);
        resultDuration1.setText(String.format("Em %d dias", diasProximaMenstruacao));

        Log.i(tag, "Dias restantes para a próxima menstruação: " + diasProximaMenstruacao + " dias, " + userID);
        Log.i(tag, "Dias restantes para o próximo período do ciclo: " + diasProximaFase + " dias, " + userID);

        float progressoSeekBarDiaDoCiclo = (Float.parseFloat(diaDoCiclo) / duracaoCiclo) * 100;

        int duracaoFaseAtual = 0;

        for (Map.Entry<String, Map<String, Timestamp>> entry : currentCycle.getFases().entrySet()) {
            String fase = entry.getKey();
            Map<String, Timestamp> dataFase = entry.getValue();
            Timestamp inicioFase = dataFase.get("inicio");
            Timestamp fimFase = dataFase.get("fim");
            if (fase.equals(nomeFase)) {
                duracaoFaseAtual = (int) ((fimFase.toDate().getTime() - inicioFase.toDate().getTime()) / (1000 * 60 * 60 * 24)) + 1;
                break;
            }
        }

        float progressoSeekBarProximaFase = ((duracaoFaseAtual - diasProximaFase) / (float) duracaoFaseAtual) * 100;
        float progressoSeekBarProximoSangramento = ((duracaoCiclo - diasProximaMenstruacao) / (float) duracaoCiclo) * 100;

        seekBar.setProgress(progressoSeekBarDiaDoCiclo);
        seekBarPeriod.setProgress(progressoSeekBarProximaFase);
        seekBarNextBleeding.setProgress(progressoSeekBarProximoSangramento);

        Log.i(tag, "Ciclo atual recuperado e refletido na linha do tempo: " + currentCycle.getId());
    }

    private boolean isCicloAtualTerminado(Cycle ciclo) {
        Calendar hoje = Calendar.getInstance();
        Log.i(tag, "Ciclo atual foi finalizado? " + (hoje.getTimeInMillis() > ciclo.getEndDate().toDate().getTime()));
        return hoje.getTimeInMillis() > ciclo.getEndDate().toDate().getTime();
    }

    private void carregandoBotoesAppCompatButton(int buttonId, Class<?> targetActivity) {
        AppCompatButton button = findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimeLine.this, targetActivity);
                startActivity(intent);
                finish();
            }
        });
    }

    private void carregandoBotoesAppCompatImageButton(int buttonId, Class<?> targetActivity) {
        AppCompatImageButton button = findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimeLine.this, targetActivity);
                startActivity(intent);
                finish();
            }
        });
    }
}
