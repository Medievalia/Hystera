package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

    private final String tag = "TimeLineClass";
    private String userID;
    private CircularSeekBar seekBar;
    private CircularSeekBar seekBarPeriod;
    private CircularSeekBar seekBarNextBleeding;
    private Cycle currentCycle;
    private int cycleAverage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linha_tempo);

        NotificationHelper notificationHelper = new NotificationHelper(this);
        notificationHelper.sendImmediateNotification("View Aberta", "Você abriu a tela de Example.");

        //navegação e menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationHelper menuHelper = new BottomNavigationHelper();
        menuHelper.setNavigationFocus(bottomNavigationView, R.id.nav_seekbar);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_anota) {
                    startActivity(new Intent(TimeLine.this, Notes.class));
                    startActivity(new Intent(TimeLine.this, Annotations.class));
                    return true;
                } else if (id == R.id.nav_calendario) {
                    startActivity(new Intent(TimeLine.this, CalendarCycle.class));
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
                        Timestamp startDate = Timestamp.now();

                        Map<String, Map<String, Timestamp>> fases = (Map<String, Map<String, Timestamp>>) actualCycle.get("fases");
                        if (fases != null) {
                            Map<String, Timestamp> faseMenstrual = fases.get("Menstrual");
                            if (faseMenstrual != null) {
                                Timestamp inicioMenstrual = faseMenstrual.get("inicio");
                                Timestamp fimMenstrual = faseMenstrual.get("fim");
                                ultimoCiclo.adicionarFase("Menstrual", inicioMenstrual, fimMenstrual);
                                startDate = inicioMenstrual;
                            }

                            Map<String, Timestamp> faseFolicular = fases.get("Folicular");
                            if (faseMenstrual != null) {
                                Timestamp inicioFolicular = faseFolicular.get("inicio");
                                Timestamp fimFolicular = faseFolicular.get("fim");
                                ultimoCiclo.adicionarFase("Folicular", inicioFolicular, fimFolicular);
                            }

                            Map<String, Timestamp> faseOvulacao = fases.get("Ovulacao");
                            if (faseMenstrual != null) {
                                Timestamp inicioOvulacao = faseOvulacao.get("inicio");
                                Timestamp fimOvulacao = faseOvulacao.get("fim");
                                ultimoCiclo.adicionarFase("Ovulacao", inicioOvulacao, fimOvulacao);
                            }

                            Map<String, Timestamp> faseLutea = fases.get("Lutea");
                            if (faseMenstrual != null) {
                                Timestamp inicioLutea = faseLutea.get("inicio");
                                Timestamp fimLutea = faseLutea.get("fim");
                                ultimoCiclo.adicionarFase("Lutea", inicioLutea, fimLutea);
                            }
                        }
                        ultimoCiclo.setStartDate(startDate);
                        ultimoCiclo.setDuration(actualCycle.getLong("duration").intValue());
                        ultimoCiclo.setNatural(actualCycle.getBoolean("natural"));
                        ultimoCiclo.setBleeding(actualCycle.getLong("bleeding").intValue());
                        ultimoCiclo.setUserID(userID);
                        ultimoCiclo.setId(actualCycle.getId());

                        if (ultimoCiclo != null) {
                            int maxTentativas = 12;
                            int tentativas = 0;

                            while (isCicloAtualTerminado(ultimoCiclo) && tentativas < maxTentativas) {
                                currentCycle = ultimoCiclo.simularProximoCiclo(ultimoCiclo);
                                currentCycle.salvarCicloNoFirebase();
                                ultimoCiclo = currentCycle;
                                tentativas++;
                            }

                            Log.d(tag, "Ciclos criados durante simulação: " + tentativas);

                            if (isCicloAtualTerminado(ultimoCiclo)) {
                                currentCycle = ultimoCiclo.simularProximoCiclo(ultimoCiclo);
                                currentCycle.salvarCicloNoFirebase();
                            } else {
                                currentCycle = ultimoCiclo;
                            }
                        } else {
                            Log.e(tag, "Ciclo nulo para usuário: " + userID);
                        }

                        atualizandoSeekBar(currentCycle);
                        calcularMediaDuracaoCiclos(userID);
                    } else {
                        Log.e(tag, "Nenhum ciclo encontrado.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(tag, "Erro ao recuperar o último ciclo. ", e);
                });

        carregandoBotoesAppCompatButton(R.id.button_grafico, Graph.class);
        carregandoBotoesAppCompatButton(R.id.button_notification, Notifications.class);
        carregandoBotoesAppCompatButton(R.id.button_perfil, Profile.class);
        carregandoBotoesAppCompatImageButton(R.id.button_menstruacao, NewBleeding.class);
        carregandoBotoesAppCompatImageButton(R.id.button_periodo, CycleInformation.class);

        ImageButton buttonMenstruacao = findViewById(R.id.button_menstruacao);
        buttonMenstruacao.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmação")
                    .setMessage("Tem certeza que deseja registrar o início de uma nova menstruação?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        NewBleeding newBleeding = new NewBleeding();
                        calcularMediaDuracaoCiclos(userID);
                        newBleeding.validateNewCycle(userID, TimeLine.this); // Passando o contexto
                        Toast.makeText(this, "Nova menstruação registrada", Toast.LENGTH_SHORT).show();
                        atualizarCiclo();
                    })
                    .setNegativeButton("Não", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
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
        } else if (nomeFase.equalsIgnoreCase("Lutea")) {
            resultDuration.setText("Fase Lútea");
        } else {
            resultDuration.setText(String.format("Fase %s", nomeFase));
        }

        Map<String, Integer> diasRestantes = currentCycle.calcularDiasRestantes();
        int diasProximaMenstruacao = diasRestantes.get("diasProximaMenstruacao") + 1;
        int diasProximaFase = diasRestantes.get("diasProximaFase") + 1;

        MaterialTextView resultDuration1 = findViewById(R.id.result_duration1);
        if (diasProximaMenstruacao > 1){
            resultDuration1.setText(String.format("Em %d dias", diasProximaMenstruacao));
        } else {
            resultDuration1.setText(String.format("Em %d dia", diasProximaMenstruacao));
        }

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
            }
        });
    }

    public void calcularMediaDuracaoCiclos(String userID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Usuarios").document(userID)
                .collection("Ciclos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        int somaDuracoes = 0;
                        int quantidadeCiclos = 0;

                        for (DocumentSnapshot cicloDoc : queryDocumentSnapshots.getDocuments()) {
                            if (cicloDoc.contains("duration")) {
                                int duracao = cicloDoc.getLong("duration").intValue();
                                somaDuracoes += duracao;
                                quantidadeCiclos++;
                            }
                        }

                        if (quantidadeCiclos > 0) {
                            int mediaDuracao = (int) Math.round((double) somaDuracoes / quantidadeCiclos);
                            Log.i(tag, "Média da duração dos ciclos: " + mediaDuracao);
                            db.collection("Usuarios").document(userID)
                                    .update("Cycle Average", mediaDuracao)
                                    .addOnSuccessListener(aVoid -> Log.i(tag, "Média atualizada com sucesso!"))
                                    .addOnFailureListener(e -> Log.e(tag, "Erro ao atualizar a média.", e));
                        } else {
                            Log.e(tag, "Nenhum ciclo encontrado com duração.");
                        }
                    } else {
                        Log.e(tag, "Nenhum ciclo encontrado para o usuário.");
                    }
                })
                .addOnFailureListener(e -> Log.e(tag, "Erro ao buscar ciclos.", e));
    }


    private void atualizarCiclo() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                            currentCycle = ultimoCiclo;
                            atualizandoSeekBar(currentCycle);
                        } else {
                            Log.e(tag, "Ciclo nullo para usuário: " + userID);
                        }
                    } else {
                        Log.e(tag, "Nenhum ciclo encontrado.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(tag, "Erro ao recuperar o último ciclo. ", e);
                });
    }
}
