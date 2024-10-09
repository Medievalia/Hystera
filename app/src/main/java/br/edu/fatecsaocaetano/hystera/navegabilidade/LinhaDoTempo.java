package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import br.edu.fatecsaocaetano.hystera.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import me.tankery.lib.circularseekbar.CircularSeekBar;

public class LinhaDoTempo extends AppCompatActivity {

    private final String tag = "LinhaDoTempoClass";
    private String userID;
    private Timestamp today;
    private CircularSeekBar seekBar;
    private CircularSeekBar seekBarPeriod;
    private CircularSeekBar seekBarNextBleeding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linha_tempo);
        seekBar = findViewById(R.id.seekbar);
        seekBarPeriod = findViewById(R.id.seekbar_period);
        seekBarNextBleeding = findViewById(R.id.seekbar_next);

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
                        DocumentSnapshot currentCycle = queryDocumentSnapshots.getDocuments().get(0);
                        Cycle ultimoCiclo = currentCycle.toObject(Cycle.class);

                        // Verificar se o ciclo atual terminou
                        if (isCicloAtualTerminado(ultimoCiclo)) {
                            Cycle novoCiclo = ultimoCiclo.simularProximoCiclo(ultimoCiclo);
                            novoCiclo.salvarCicloNoFirebase(); // Salvar o novo ciclo no Firebase
                        } else {
                            // Continue com a lógica existente
                            Map<String, String> informacoesCiclo = ultimoCiclo.obterInformacoesDoCicloAtual();

                            int duracaoCiclo = ultimoCiclo.getDuration();
                            int diasMenstruacao = ultimoCiclo.getBleeding();

                            String nomeFase = informacoesCiclo.get("faseAtual");
                            String diaDoCiclo = informacoesCiclo.get("diaDoCiclo");

                            Map<String, Integer> diasRestantes = ultimoCiclo.calcularDiasRestantes();
                            int diasProximaMenstruacao = diasRestantes.get("diasProximaMenstruacao");
                            int diasProximaFase = diasRestantes.get("diasProximaFase");

                            float progressoSeekBarDiaDoCiclo = (Float.parseFloat(diaDoCiclo) / duracaoCiclo) * 100;

                            int diasFaltandoParaProximaFase = diasProximaFase;
                            int duracaoFaseAtual = 0;

                            for (Map.Entry<String, Map<String, Timestamp>> entry : ultimoCiclo.getFases().entrySet()) {
                                String fase = entry.getKey();
                                Map<String, Timestamp> datasFase = entry.getValue();
                                Timestamp inicioFase = datasFase.get("inicio");
                                Timestamp fimFase = datasFase.get("fim");

                                if (fase.equals(nomeFase)) {
                                    duracaoFaseAtual = (int) ((fimFase.toDate().getTime() - inicioFase.toDate().getTime()) / (1000 * 60 * 60 * 24)) + 1;
                                    break;
                                }
                            }

                            float progressoSeekBarProximaFase = ((duracaoFaseAtual - diasFaltandoParaProximaFase) / (float) duracaoFaseAtual) * 100;
                            float progressoSeekBarProximoSangramento = ((duracaoCiclo - diasProximaMenstruacao) / (float) duracaoCiclo) * 100;

                            seekBar.setProgress(progressoSeekBarDiaDoCiclo);
                            seekBarPeriod.setProgress(progressoSeekBarProximaFase);
                            seekBarNextBleeding.setProgress(progressoSeekBarProximoSangramento);

                            Log.i(tag, "Último ciclo recuperado: " + ultimoCiclo.getId());
                        }
                    } else {
                        Log.e(tag, "Nenhum ciclo encontrado.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(tag, "Erro ao recuperar o último ciclo. ", e);
                });
    }

    private boolean isCicloAtualTerminado(Cycle ciclo) {
        Calendar hoje = Calendar.getInstance();
        return hoje.getTimeInMillis() > ciclo.getEndDate().toDate().getTime();
    }
}
